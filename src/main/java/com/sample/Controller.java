package com.sample;

import java.util.concurrent.atomic.AtomicLong;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.auth.oauth2.TokenResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import com.sample.repository.AccountRepository;
import com.sample.repository.VendorRepository;
import com.sample.model.Account;
import com.sample.model.Vendor;
import com.sample.service.xero.XeroService;
import com.sample.service.xero.XeroToken;

import com.xero.models.accounting.Accounts;
import com.xero.models.accounting.Contacts;

/**
 * TODO: separate xero authentication and api calls to different controllers
 */
@RestController
public class Controller {
  
  private static final Logger logger = LoggerFactory.getLogger(Controller.class);

  @Autowired
  private XeroService xeroService;

  @Autowired
  private AccountRepository accountRepository;

  @Autowired
  private VendorRepository vendorRepository;

  @RequestMapping("/")
  public RedirectView login() throws IOException{
    return new RedirectView(xeroService.getDialogUrl());
  }

  @RequestMapping("/callback")
  public ResponseEntity calllback(@RequestParam(value="code") String code) throws IOException {
    if (code == null)
    {
       return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Request: missing code");
    }

    if (xeroService.getToken(code) == null) {
      // TODO: Improve error handling for different failures
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unable to retrieve token.");
    }

    return ResponseEntity.ok("Successfully Logged In! Go to http://localhost:9000/accounts for list of accounts or http://localhost:9000/vendors for list of vendors");
  }

  // TODO: better error handling. For example, when the call to Xero fails
  @RequestMapping("/accounts")
  public List<Account> getAccounts() throws IOException {
    Accounts accounts = xeroService.getAccounts();
    if (accounts != null && accounts.getAccounts() != null) {
      accounts.getAccounts().forEach(a -> {
        List<Account> result = accountRepository.findByAccountId(a.getAccountID().toString());
        Account accnt;

        if (result == null || result.size() == 0) {
          accnt = new Account();
          accnt.setAccountId(a.getAccountID().toString());
        } else {
          accnt = result.get(0);
        }

        // set fields and save
        accnt.setCode(a.getCode());
        accnt.setName(a.getName());
        accnt.setType(a.getType().toString());
        accnt.setStatus(a.getStatus().toString());
        accnt.setDescription(a.getDescription());
        accnt.setEnablePaymentsToAccount(a.getEnablePaymentsToAccount().booleanValue());

        accountRepository.save(accnt);
      });
    }
    
    return accountRepository.findAll();
  }

  @RequestMapping("/vendors")
  public List<Vendor> getVendors() throws IOException {
    Contacts contacts = xeroService.getContacts();

    if (contacts != null && contacts.getContacts() != null) {
      contacts.getContacts().forEach(v -> {
        List<Vendor> result = vendorRepository.findByContactId(v.getContactID().toString());
        Vendor vndr;

        if (result == null || result.size() == 0) {
          vndr = new Vendor();
          vndr.setContactId(v.getContactID().toString());
        } else {
          vndr = result.get(0);
        }

        // set fields and save
        vndr.setContactNumber(v.getContactNumber());
        vndr.setName(v.getName());

        vendorRepository.save(vndr);
      });
    }
    
    return vendorRepository.findAll();
  }
}
