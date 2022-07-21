package com.sample.service.xero;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.MemoryDataStoreFactory;

import com.xero.api.ApiClient;
import com.xero.api.client.IdentityApi;
import com.xero.models.identity.Connection;
import com.xero.api.ApiClient;
import com.xero.api.XeroApiException;
import com.xero.api.client.AccountingApi;
import com.xero.models.accounting.*;

import com.sample.service.xero.XeroService;

@Service
public class XeroServiceImpl implements XeroService {
  private static final Logger logger = LoggerFactory.getLogger(XeroServiceImpl.class);

  @Value("${xero.client.id}")
  private String clientId;
  @Value("${xero.client.secret}")
  private String clientSecret;
  @Value("${xero.redirect.uri}")
  private String redirectURI;

  // TODO: Move links to configuration or database
  private final String TOKEN_SERVER_URL = "https://identity.xero.com/connect/token";
  private final String AUTHORIZATION_SERVER_URL = "https://login.xero.com/identity/connect/authorize";
  private final NetHttpTransport HTTP_TRANSPORT = new NetHttpTransport();
  private final JsonFactory JSON_FACTORY = new JacksonFactory();

  /**
   * TODO: Store XeroToken to Cache such as Redis/memcached.
   * Normally, a customer or profile will be created after the user logs into my application.
   * So each customer will login and have their own session and each session will have their own 
   * XeroToken. Currently, for this application, we'll share
   * the token since the only purpose is to retrieve the data and store them into disk.
   *
   * TODO: Make 3rd party API calls Async
   * 
   * TODO: Refresh Token
   */
  private XeroToken token;

  public XeroServiceImpl() {
  }

  /**
   * Step 1 in the Authentication Flow.
   * Generate login dialog url. Redirect the user to this login dialog url.
   */
  public String getDialogUrl() throws IOException {
    // TODO: move to cache and validate in tokenRequest() to avoid forgery attack
    String secretState = "secret" + new Random().nextInt(999_999);

    ArrayList<String> scopeList = new ArrayList<String>();
    scopeList.add("openid");
    scopeList.add("email");
    scopeList.add("profile");
    scopeList.add("offline_access");
    scopeList.add("accounting.settings");
    scopeList.add("accounting.contacts");

    DataStoreFactory DATA_STORE_FACTORY = new MemoryDataStoreFactory();
    AuthorizationCodeFlow flow = new AuthorizationCodeFlow.Builder(BearerToken.authorizationHeaderAccessMethod(),
            HTTP_TRANSPORT, JSON_FACTORY, new GenericUrl(TOKEN_SERVER_URL),
            new ClientParametersAuthentication(clientId, clientSecret), clientId, AUTHORIZATION_SERVER_URL)
            .setScopes(scopeList).setDataStoreFactory(DATA_STORE_FACTORY).build();

    String url = flow.newAuthorizationUrl().setClientId(clientId).setScopes(scopeList).setState(secretState)
            .setRedirectUri(redirectURI).build();

    return url;
  }

  /**
   * Step 2 in the Authentication Flow.
   * Exchange code for an auth token.
   * TODO: Add new Exceptions or add custom error codes in XeroToken class for better handling. 
   * For example, if code is null, return a different exception
   * so the caller will know what the error is
   */
  public XeroToken getToken(String code) throws IOException {
    if (code == null) {
      return null;
    }

    ArrayList<String> scopeList = new ArrayList<String>();
    scopeList.add("openid");
    scopeList.add("email");
    scopeList.add("profile");
    scopeList.add("offline_access");
    scopeList.add("accounting.settings");
    scopeList.add("accounting.contacts");

    DataStoreFactory DATA_STORE_FACTORY = new MemoryDataStoreFactory();

    AuthorizationCodeFlow flow = new AuthorizationCodeFlow.Builder(BearerToken.authorizationHeaderAccessMethod(),
            HTTP_TRANSPORT, JSON_FACTORY, new GenericUrl(TOKEN_SERVER_URL),
            new ClientParametersAuthentication(clientId, clientSecret), clientId, AUTHORIZATION_SERVER_URL)
            .setScopes(scopeList).setDataStoreFactory(DATA_STORE_FACTORY).build();

    TokenResponse tokenResponse = flow.newTokenRequest(code).setRedirectUri(redirectURI).execute();

    HttpTransport httpTransport = new NetHttpTransport();
    JsonFactory jsonFactory = new JacksonFactory();
    GoogleCredential credential = new GoogleCredential.Builder().setTransport(httpTransport)
            .setJsonFactory(jsonFactory).setClientSecrets(clientId, clientSecret).build();
    credential.setAccessToken(tokenResponse.getAccessToken());
    credential.setRefreshToken(tokenResponse.getRefreshToken());
    credential.setExpiresInSeconds(tokenResponse.getExpiresInSeconds());

    // Create requestFactory with credentials
    HttpTransport transport = new NetHttpTransport();
    HttpRequestFactory requestFactory = transport.createRequestFactory(credential);

    // Init IdentityApi client
    ApiClient defaultClient = new ApiClient("https://api.xero.com", null, null, null, requestFactory);
    IdentityApi idApi = new IdentityApi(defaultClient);
    List<Connection> connection = idApi.getConnections(tokenResponse.getAccessToken());

    // store token in memory
    token = new XeroToken();
    token.setJwtToken(tokenResponse.toPrettyString());
    token.setAccessToken(tokenResponse.getAccessToken());
    token.setRefreshToken(tokenResponse.getRefreshToken());
    token.setExpiresInSeconds(tokenResponse.getExpiresInSeconds().toString());
    token.setXeroTenantId(connection.get(0).getTenantId().toString());

    return token;
  }

  public Accounts getAccounts() throws IOException {

    // Init AccountingApi client
    ApiClient defaultClient = new ApiClient();

    // Get Singleton - instance of accounting client
    AccountingApi accountingApi = AccountingApi.getInstance(defaultClient);
    Accounts accounts = null;

    try {
      accounts = accountingApi.getAccounts(token.getAccessToken(),token.getXeroTenantId(),null, null, null);
    } catch (XeroApiException xe) {
      // TODO: return errors
      logger.error("Xero Exception: " + xe.getResponseCode());
      for(Element item : xe.getError().getElements()){
        for(ValidationError err : item.getValidationErrors()){
          logger.error("Error Msg: " + err.getMessage());
        }
      }
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
    return accounts;
  }

  public Contacts getContacts() throws IOException {

    // Init AccountingApi client
    ApiClient defaultClient = new ApiClient();

    // Get Singleton - instance of accounting client
    AccountingApi accountingApi = AccountingApi.getInstance(defaultClient);
    Contacts contacts = null;

    try {
      // Get All Contacts
      contacts = accountingApi.getContacts(token.getAccessToken(),token.getXeroTenantId(),null, null, null, null, null, null);
    } catch (XeroApiException xe) {
      // TODO: return errors
      logger.error("Xero Exception: " + xe.getResponseCode());
      for(Element item : xe.getError().getElements()){
        for(ValidationError err : item.getValidationErrors()){
          logger.error("Error Msg: " + err.getMessage());
        }
      }
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
    return contacts;
  }
}
