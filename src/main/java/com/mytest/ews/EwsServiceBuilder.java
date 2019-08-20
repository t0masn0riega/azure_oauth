package com.mytest.ews;

import java.net.URI;
import java.net.URISyntaxException;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.misc.ConnectingIdType;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import microsoft.exchange.webservices.data.misc.ImpersonatedUserId;
import org.springframework.stereotype.Component;

@Component
public class EwsServiceBuilder {

  public ExchangeService build(String accessToken) throws Exception {

    ExchangeService exchangeService = new ExchangeService();
    exchangeService.getHttpHeaders().put("Authorization", "Bearer " + accessToken);
    exchangeService.setUrl(new URI("https://outlook.office365.com/EWS/Exchange.asmx"));
    setImpersonation(exchangeService);
    return  exchangeService;
  }

  public ExchangeService build(String username, String password) throws URISyntaxException {

    ExchangeService exchangeService = new ExchangeService();
    exchangeService.setCredentials(new WebCredentials(username, password));
    exchangeService.setUrl(new URI("https://outlook.office365.com/EWS/Exchange.asmx"));
    setImpersonation(exchangeService);
    return  exchangeService;
  }

  private void setImpersonation(ExchangeService exchangeService) throws URISyntaxException {
    ImpersonatedUserId user = new ImpersonatedUserId(ConnectingIdType.SmtpAddress, "mytest@nmicrosoft.com");
    exchangeService.setImpersonatedUserId(user);
  }

}
