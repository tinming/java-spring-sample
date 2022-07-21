package com.sample.service.xero;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInfo;
import static org.junit.Assert.*;

import com.sample.service.xero.XeroToken;
import com.google.api.client.auth.oauth2.TokenResponseException;


/**
 * Used to test 3rd party Xero API calls.
 * TODO: create an external mock Xero API
 */
@ExtendWith(SpringExtension.class)
public class XeroServiceImplIntegrationTest {

  private static final Logger logger = LoggerFactory.getLogger(XeroServiceImplIntegrationTest.class);

  @BeforeEach
  void printFunctionName(TestInfo testInfo) {
      logger.info("---- Executing: " + testInfo.getDisplayName() + " ----");
  }

  /**
   * TODO: move to beanfactory configuration
   */
  @TestConfiguration
  static class XeroServiceImplTestContextConfiguration {
    @Bean
    public XeroService xeroService() {
      return new XeroServiceImpl();
    }
  }
 
  @Autowired
  private XeroService xeroService;

  
  @Test
  @DisplayName("Get Dialog URL")
  void getDialogUrl() throws Exception {
    String redirectUri = xeroService.getDialogUrl();
    assertNotNull(redirectUri);
  }

  @DisplayName("Get Token with empty code")
  @Test
  void failedGetTokenWithEmptyCode() throws Exception {
    XeroToken token = xeroService.getToken(null);
    assertNull(token);
  }

  @DisplayName("Get Token with invalid code")
  @Test
  void failedGetTokenWithInvalidCode() throws Exception {
    try {
      XeroToken token = xeroService.getToken("123");
      fail("TokenResponseException not thrown");
    } catch (TokenResponseException e) {
      assertNotNull(e.getDetails());
      assertEquals(e.getDetails().getError(), "invalid_client");
    }
  }

  // TODO: getAccounts / getVendors Success and Failed Cases
}