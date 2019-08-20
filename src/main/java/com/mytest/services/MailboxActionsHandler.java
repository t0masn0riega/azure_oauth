package com.mytest.services;

import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.mytest.ews.EwsServiceBuilder;
import com.mytest.ews.MailRetrievel;
import java.util.ArrayList;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.service.item.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MailboxActionsHandler {

  @Autowired
  private MailRetrievel mailRetrievel;

  @Autowired
  private EwsServiceBuilder ewsServiceBuilder;

  @Autowired
  private OAuthTokensStorage oAuthTokensStorage;

  public ArrayList<Item> retrieveMailitems(String code) throws Exception {

    log.info("Entering retrieveMailitems");

    IAuthenticationResult authenticationResult = oAuthTokensStorage.retrieve(code);

    log.info("authenticationResult: " + authenticationResult);
    if (authenticationResult == null || authenticationResult.accessToken() == null) {
      log.info("authenticationResult is not valid");
      return null;
    }

    log.info("Build ExchangeService");
    ExchangeService exchangeService = ewsServiceBuilder.build(authenticationResult.accessToken());

    log.info("Retrieve mail items");
    ArrayList<Item> items = mailRetrievel.execute(exchangeService);

    log.info("leaving retrieveMailitems: " + items);
    return items;

  }

}
