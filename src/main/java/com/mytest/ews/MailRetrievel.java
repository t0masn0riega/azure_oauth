package com.mytest.ews;

import java.util.ArrayList;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.PropertySet;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.service.item.Item;
import microsoft.exchange.webservices.data.search.FindItemsResults;
import microsoft.exchange.webservices.data.search.ItemView;
import org.springframework.stereotype.Service;

@Service
public class MailRetrievel {

  public MailRetrievel() {
  }

  public ArrayList<Item> execute(ExchangeService exchangeService) throws Exception {


    ItemView view = new ItemView(10);
    FindItemsResults<Item> findResults = exchangeService.findItems(WellKnownFolderName.Inbox, view);

    exchangeService.loadPropertiesForItems(findResults, PropertySet.FirstClassProperties);

    ArrayList<Item> results = findResults.getItems();

    for (Item item : findResults.getItems()) {
      // Do something with the item as shown
      System.out.println("id==========" + item.getId());
      System.out.println("sub==========" + item.getSubject());
    }

    return results;

  }

}
