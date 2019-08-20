package com.mytest.web.controller;

import com.mytest.services.MailboxActionsHandler;
import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;
import microsoft.exchange.webservices.data.core.service.item.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/v1/mailbox")
public class MailboxActionsController {

  @Autowired
  MailboxActionsHandler mailboxActionsHandler;

  @GetMapping("/list/{code}")
  public ResponseEntity<ArrayList<Item>> retrieveMailItems(@PathVariable String code) throws Exception {
    log.info("Entering - createOAuthTokens - " + code);
    ArrayList<Item> items = mailboxActionsHandler.retrieveMailitems(code);
    log.info("Leaving - createOAuthTokens");
    return new ResponseEntity<>(items, HttpStatus.OK);
  }

}
