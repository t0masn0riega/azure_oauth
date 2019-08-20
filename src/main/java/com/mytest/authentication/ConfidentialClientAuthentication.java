package com.mytest.authentication;

import com.microsoft.aad.msal4j.ClientCredentialFactory;
import com.microsoft.aad.msal4j.ClientCredentialParameters;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.microsoft.aad.msal4j.IClientCredential;
import java.net.MalformedURLException;
import java.util.Set;
import java.util.stream.Collectors;

public class ConfidentialClientAuthentication {
  private static String applicationId;
  private static String secret;
  private static String tenantId;
  // Set authority to allow only organizational accounts
  // Device code flow only supports organizational accounts
  // https://login.microsoftonline.com/29f170a8-e66c-416c-ba2b-240e4936ebc2/
  // https://login.microsoftonline.com/organizations/
  private static String authority;
  private static String authorityFormat = "https://login.microsoftonline.com/%s/";

  public static void initialize(String applicationId, String secret, String tenantId) {
    ConfidentialClientAuthentication.applicationId = applicationId;
    ConfidentialClientAuthentication.secret = secret;
    ConfidentialClientAuthentication.tenantId = tenantId;
    ConfidentialClientAuthentication.authority = String.format(authorityFormat, ConfidentialClientAuthentication.tenantId);
  }

  public static String getAccessToken(String[] scopes) {
    if (applicationId == null || secret == null) {
      System.out.println("You must initialize PublicClientAuthentication before calling getUserAccessToken");
      return null;
    }

    Set<String> scopeSet = Set.of(scopes);

    IClientCredential credential = ClientCredentialFactory.create(secret);
    System.out.println("applicationId:[" + applicationId + "]");
    System.out.println("secret:[" + secret + "]");
    System.out.println("authority:[" + authority + "]");
    System.out.println("scopes:[" + scopeSet.stream().collect(Collectors.joining(",")) + "]");

    ConfidentialClientApplication app;
    try {
      // Build the MSAL application object with
      // app ID and authority
      app = ConfidentialClientApplication.builder(applicationId, credential)
          .authority(authority)
          .build();
    } catch (MalformedURLException e) {
      return null;
    }

    ClientCredentialParameters clientCredentialParameters = ClientCredentialParameters.builder(scopeSet).build();

    // Request a token, passing the requested permission scopes
    IAuthenticationResult result = app.acquireToken(clientCredentialParameters
    ).exceptionally(ex -> {
      System.out.println("Unable to authenticate - " + ex.getMessage());
      return null;
    }).join();

    if (result != null) {
      return result.accessToken();
    }

    return null;
  }

}
