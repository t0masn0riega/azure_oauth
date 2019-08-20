package com.mytest.ui;

import com.mytest.authentication.ConfidentialClientAuthentication;
import com.mytest.authentication.PublicClientAuthentication;
import com.mytest.ews.EwsServiceBuilder;
import com.mytest.ews.MailRetrievel;
import com.mytest.graph.Graph;
import com.microsoft.graph.models.extensions.DateTimeTimeZone;
import com.microsoft.graph.models.extensions.Event;
import com.microsoft.graph.models.extensions.User;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import microsoft.exchange.webservices.data.core.ExchangeService;

public class VisionGraph {
  private String tenantId;
  private String appId;
  private String[] appScopes;
  private String secret;
  private String impersonatorUsername;
  private String impersonatorPassword;
  private static String OAUTH_PROPERTY_FILE = "oAuth-vision-ews.properties";
//  private static String OAUTH_PROPERTY_FILE = "oAuth-vision-ingester.properties";

  public static void main(String[] args) {
    new VisionGraph().executer();
  }

  public void executer() {
    System.out.println("Java Graph Tutorial");
    System.out.println();

    loadOauthProperties();

    boolean isPublic = true;

    // Get an access token
    final String accessToken = retrieveClientAccessToken(isPublic);
    System.out.println("accessToken:[" + accessToken + "]");

    if (isPublic) {
//      User user = retrieveUser(accessToken);
    }

    Scanner input = new Scanner(System.in);

    int choice = -1;

    while (choice != 0) {
      System.out.println("Please choose one of the following options:");
      System.out.println("0. Exit");
      System.out.println("1. Display access token");
      System.out.println("2. List calendar events");
      System.out.println("3. List all users");
      System.out.println("4. List emails for user with user/password");
      System.out.println("5. List emails for user with token");

      try {
        choice = input.nextInt();
      } catch (InputMismatchException ex) {
        // Skip over non-integer input
        input.nextLine();
      }

      // Process user choice
      switch(choice) {
        case 0:
          // Exit the program
          System.out.println("Goodbye...");
          break;
        case 1:
          // Display access token
          System.out.println("Access token: " + accessToken);
          break;
        case 2:
          // List the
          listCalendarEvents(accessToken);
          break;
        case 3:
          // List the
          listAllUsers(accessToken);
          break;
        case 4:
          // List the
          listInboxEmailsForUser();
          break;
        case 5:
          // List the
          listInboxEmailsForUser(accessToken);
          break;
        default:
          System.out.println("Invalid choice");
      }
    }

    input.close();
  }

  private String retrieveClientAccessToken(boolean isPublic) {
    if (isPublic) {
      return retrievePublicClientAccessToken();
    }

    return retrieveConfidfentialClientAccessToken();

  }

  private String retrievePublicClientAccessToken() {
//    PublicClientAuthentication.initialize(appId, tenantId);
//    return PublicClientAuthentication.getUserAccessToken(appScopes);
    return null;
  }

  private String retrieveConfidfentialClientAccessToken() {
    ConfidentialClientAuthentication.initialize(appId, secret, tenantId);
    return ConfidentialClientAuthentication.getAccessToken(appScopes);
  }

  public void loadOauthProperties() {
// Load OAuth settings
    final Properties oAuthProperties = new Properties();
    try {
      oAuthProperties.load(getClass().getClassLoader().getResourceAsStream(OAUTH_PROPERTY_FILE));
    } catch (IOException e) {
      System.out.println("Unable to read OAuth configuration. Make sure you have a properly formatted oAuth.properties file. See README for details.");
      return;
    }

    tenantId = oAuthProperties.getProperty("tenant.id");
    appId = oAuthProperties.getProperty("app.id");
    appScopes = oAuthProperties.getProperty("app.scopes").split(",");
    secret = oAuthProperties.getProperty("app.secret");
    impersonatorUsername = oAuthProperties.getProperty("app.impersonator.username");
    impersonatorPassword = oAuthProperties.getProperty("app.impersonator.password");
  }

  private User retrieveUser(String accessToken) {
// Greet the user
    User user = Graph.getUser(accessToken);
    System.out.println("Welcome " + user.displayName);
    System.out.println();

    return user;
  }

  private void listAllUsers(String accessToken) {
// Greet the user
    List<User> users = Graph.getUsers(accessToken);

    if (users == null || users.isEmpty()) {
      System.out.println("Not users to display: " + users);
    }
    for (User user: users) {
      System.out.println("displayName: " + user.displayName);
      System.out.println("mail: " + user.mail);
      System.out.println("id: " + user.id);
      System.out.println();
    }
  }

  private void listCalendarEvents(String accessToken) {
    // Get the user's events
    List<Event> events = Graph.getEvents(accessToken);

    System.out.println("Events:");

    for (Event event : events) {
      System.out.println("Subject: " + event.subject);
      System.out.println("  Organizer: " + event.organizer.emailAddress.name);
      System.out.println("  Start: " + formatDateTimeTimeZone(event.start));
      System.out.println("  End: " + formatDateTimeTimeZone(event.end));
    }

    System.out.println();
  }

  private void listInboxEmailsForUser() {
    try {
      ExchangeService exchangeService = new EwsServiceBuilder().build(impersonatorUsername, impersonatorPassword);
      new MailRetrievel().execute(exchangeService);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void listInboxEmailsForUser(String accessToken) {
    try {
      ExchangeService exchangeService = new EwsServiceBuilder().build(accessToken);
      new MailRetrievel().execute(exchangeService);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private String formatDateTimeTimeZone(DateTimeTimeZone date) {
    LocalDateTime dateTime = LocalDateTime.parse(date.dateTime);

    return dateTime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)) + " (" + date.timeZone + ")";
  }
}
