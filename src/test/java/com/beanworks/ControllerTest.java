package com.sample;

import java.util.stream.IntStream;
import java.util.UUID;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.sample.repository.AccountRepository;
import com.sample.repository.VendorRepository;
import com.sample.service.xero.XeroService;

import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInfo;
import static org.junit.Assert.*;

import com.xero.models.accounting.Accounts;
import com.xero.models.accounting.Account;
import com.xero.models.accounting.Account.StatusEnum;
import com.xero.models.accounting.AccountType;

@ExtendWith(SpringExtension.class)
@WebMvcTest(Controller.class)
public class ControllerTest {

  private static final Logger logger = LoggerFactory.getLogger(ControllerTest.class);
  private static Accounts accounts = new Accounts();

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private XeroService xeroService;

  @MockBean
  private AccountRepository accountRepository;

  @MockBean
  private VendorRepository vendorRepository;

  @BeforeEach
  void printFunctionName(TestInfo testInfo) {
      logger.info("---- Executing: " + testInfo.getDisplayName() + " ----");
  }

  @BeforeAll
  static void setUp(TestInfo testInfo) {
    IntStream.range(0, 10).forEach(
      nbr -> {
        Account a = new Account();
        a.setAccountID(UUID.randomUUID());
        a.setType(AccountType.PREPAYMENT);
        a.setStatus(StatusEnum.ACTIVE);
        a.setEnablePaymentsToAccount(false);
        
        accounts.addAccountsItem(a);
      }
    );
  }

  @Test
  void accountsSuccessfulReturnList() throws Exception {
    when(xeroService.getAccounts()).thenReturn(accounts);
    when(accountRepository.save(any(com.sample.model.Account.class))).then(returnsFirstArg());
    when(accountRepository.findByAccountId(any(String.class))).thenReturn(null);
    when(accountRepository.findAll()).thenReturn(new ArrayList());
    this.mockMvc.perform(get("/accounts")).andDo(print()).andExpect(status().isOk());
  }

  // TODO: Add test for vendors
}