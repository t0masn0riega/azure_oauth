package com.mytest.graph;

import com.mytest.authentication.SimpleAuthProvider;
import com.microsoft.graph.logger.DefaultLogger;
import com.microsoft.graph.logger.LoggerLevel;
import com.microsoft.graph.models.extensions.Event;
import com.microsoft.graph.models.extensions.IGraphServiceClient;
import com.microsoft.graph.models.extensions.User;
import com.microsoft.graph.options.Option;
import com.microsoft.graph.options.QueryOption;
import com.microsoft.graph.requests.extensions.GraphServiceClient;
import com.microsoft.graph.requests.extensions.IEventCollectionPage;
import com.microsoft.graph.requests.extensions.IUserCollectionPage;
import java.util.LinkedList;
import java.util.List;

public class Graph {

  private static IGraphServiceClient graphClient = null;
  private static SimpleAuthProvider authProvider = null;

  private static void ensureGraphClient(String accessToken) {
    if (graphClient == null) {
      // Create the auth provider
      authProvider = new SimpleAuthProvider(accessToken);

      // Create default logger to only log errors
      DefaultLogger logger = new DefaultLogger();
      logger.setLoggingLevel(LoggerLevel.ERROR);

      // Build a Graph client
      graphClient = GraphServiceClient.builder()
          .authenticationProvider(authProvider)
          .logger(logger)
          .buildClient();
    }
  }

  public static User getUser(String accessToken) {
    ensureGraphClient(accessToken);

    // GET /me to get authenticated user
    User me = graphClient
        .me()
        .buildRequest()
        .get();

    return me;
  }

  public static List<User> getUsers(String accessToken) {
    ensureGraphClient(accessToken);

    List<User> users = null;

    // GET /me to get authenticated user
    IUserCollectionPage userPages = graphClient
        .users()
        .buildRequest()
        .get();

    if (!userPages.getCurrentPage().isEmpty()) {
      users = userPages.getCurrentPage();
    }

    return users;
  }

  public static List<Event> getEvents(String accessToken) {
    ensureGraphClient(accessToken);

    // Use QueryOption to specify the $orderby query parameter
    final List<Option> options = new LinkedList<Option>();
    // Sort results by createdDateTime, get newest first
    options.add(new QueryOption("orderby", "createdDateTime DESC"));

    // GET /me/events
    IEventCollectionPage eventPage = graphClient
        .me()
        .events()
        .buildRequest(options)
        .select("subject,organizer,start,end")
        .get();

    return eventPage.getCurrentPage();
  }
}