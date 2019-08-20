package com.mytest.authentication;

import com.microsoft.aad.msal4j.IAccount;
import com.microsoft.aad.msal4j.ITokenCacheAccessAspect;
import com.microsoft.aad.msal4j.ITokenCacheAccessContext;
import com.microsoft.aad.msal4j.SilentParameters;
import java.net.MalformedURLException;
import java.util.Set;

import com.microsoft.aad.msal4j.DeviceCode;
import com.microsoft.aad.msal4j.DeviceCodeFlowParameters;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.microsoft.aad.msal4j.PublicClientApplication;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PublicClientAuthentication {
  private static String applicationId;
  private static String tenantId;
  // Set authority to allow only organizational accounts
  // Device code flow only supports organizational accounts
  // https://login.microsoftonline.com/29f170a8-e66c-416c-ba2b-240e4936ebc2/
  // https://login.microsoftonline.com/organizations/
  private static String authority;
  private static String authorityFormat = "https://login.microsoftonline.com/%s/";

  private static String data;

  class EwsTokenCacheAccessAspect implements ITokenCacheAccessAspect {

    @Override
    public void beforeCacheAccess(ITokenCacheAccessContext iTokenCacheAccessContext) {
      log.info("EwsTokenCacheAccessAspect- beforeCacheAccess: " + iTokenCacheAccessContext);
      iTokenCacheAccessContext.tokenCache().deserialize(data);
    }

    @Override
    public void afterCacheAccess(ITokenCacheAccessContext iTokenCacheAccessContext) {
      log.info("EwsTokenCacheAccessAspect- afterCacheAccess: " + iTokenCacheAccessContext);
      data = iTokenCacheAccessContext.tokenCache().serialize();
    }
  }

  public void initialize(String applicationId, String tenantId) {
    PublicClientAuthentication.applicationId = applicationId;
    PublicClientAuthentication.tenantId = tenantId;
    PublicClientAuthentication.authority = String.format(authorityFormat, PublicClientAuthentication.tenantId);
  }


  public IAuthenticationResult getAuthenticationResult(String[] scopes, Consumer<DeviceCode> deviceCodeConsumer) {
    if (applicationId == null || tenantId == null) {
      log.info("You must initialize PublicClientAuthentication before calling getUserAccessToken");
      return null;
    }

    Set<String> scopeSet = Set.of(scopes);

    log.info("applicationId:[" + applicationId + "]");
    log.info("authority:[" + authority + "]");
    log.info("scopes:[" + scopeSet.stream().collect(Collectors.joining(",")) + "]");

    PublicClientApplication app;
    try {
      // Build the MSAL application object with
      // app ID and authority
      app = PublicClientApplication.builder(applicationId)
          .authority(authority)
          .setTokenCacheAccessAspect(new EwsTokenCacheAccessAspect())
          .build();
    } catch (MalformedURLException e) {
      log.error("MalformedURLException - ", e);
      return null;
    }

    // Request a token, passing the requested permission scopes
    log.info("About to execute the 'acquireToken'");
    IAuthenticationResult result = app.acquireToken(
        DeviceCodeFlowParameters
            .builder(scopeSet, deviceCodeConsumer)
            .build()
    ).exceptionally(ex -> {
      log.info("Unable to authenticate - " + ex.getMessage());
      return null;
    }).join();

    log.info("After executing the 'acquireToken' - result:" + result);

    return result;
  }


  public IAuthenticationResult getAuthenticationResult(String[] scopes, IAccount account) {
    if (applicationId == null || tenantId == null) {
      log.info("You must initialize PublicClientAuthentication before calling getUserAccessToken");
      return null;
    }

    Set<String> scopeSet = Set.of(scopes);

    log.info("applicationId:[" + applicationId + "]");
    log.info("authority:[" + authority + "]");
    log.info("scopes:[" + scopeSet.stream().collect(Collectors.joining(",")) + "]");

    SilentParameters silentParameters =  SilentParameters.builder(
        scopeSet, account).build();


    PublicClientApplication app;
    try {
      // Build the MSAL application object with
      // app ID and authority
      app = PublicClientApplication.builder(applicationId)
          .authority(authority)
          .setTokenCacheAccessAspect(new EwsTokenCacheAccessAspect())
          .build();
    } catch (MalformedURLException e) {
      log.error("MalformedURLException - ", e);
      return null;
    }

    // Request a token, passing the requested permission scopes
    log.info("About to execute the 'acquireTokenSilently'");
    IAuthenticationResult result = null;
    try {
      result = app.acquireTokenSilently(silentParameters)
          .exceptionally(ex -> {
            log.info("Unable to authenticate - " + ex.getMessage());
            return null;
          }
          ).join();
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }

    log.info("After executing the 'acquireTokenSilently' - result:" + result);

    return result;
  }
}
