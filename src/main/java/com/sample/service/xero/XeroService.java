package com.sample.service.xero;

import java.io.IOException;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.xero.models.accounting.*;
import com.sample.service.xero.XeroToken;
import org.springframework.stereotype.Service;

public interface XeroService {

  public String getDialogUrl() throws IOException;
  public XeroToken getToken(String code) throws IOException;
  public Accounts getAccounts() throws IOException;
  public Contacts getContacts() throws IOException;
}