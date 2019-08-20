package com.mytest.services;

import com.microsoft.aad.msal4j.DeviceCode;
import com.microsoft.aad.msal4j.IAccount;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.mytest.authentication.PublicClientAuthentication;
import java.util.UUID;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

@Slf4j
@Service
public class OAuthTokensHandler {

  @Value( "${mytest.azure.tenantId}")
  private String tenantId;
  @Value( "${mytest.azure.appId}")
  private String appId;
  @Value( "${mytest.azure.scopes}")
  private String[] appScopes;
  @Value( "${mytest.azure.secret}")
  private String secret;

  @Autowired
  private OAuthTokensStorage authTokensStorage;

  @Autowired
  PublicClientAuthentication publicClientAuthentication;

  private class UserDeviceCodeWrapper {
    private String userDeviceCode;

    public String getUserDeviceCode() {
      return userDeviceCode;
    }

    public void setUserDeviceCode(String userDeviceCode) {
      this.userDeviceCode = userDeviceCode;
    }
  }

  @Async
  public void initiateCodeFlow(DeferredResult<ResponseEntity<DeviceCode>> output) {
    log.info("Entering initiateCodeFlow.");

    UserDeviceCodeWrapper userDeviceCode = new UserDeviceCodeWrapper();

    log.info("tenantId: " + tenantId);
    log.info("appId: " + appId);
    log.info("secret: " + secret);
    for (String scope: appScopes) {
      log.info("scope: " + scope);
    }

    Consumer<DeviceCode> deviceCodeConsumer = deviceCode -> {
      userDeviceCode.setUserDeviceCode(deviceCode.userCode());
      log.info("From consumer: " + userDeviceCode.getUserDeviceCode() + " " + deviceCode.message());
      output.setResult(new ResponseEntity<>(deviceCode, HttpStatus.OK));
    };

    startPublicClientAuthentication(deviceCodeConsumer, userDeviceCode);

    log.info("Leaving initiateCodeFlow.");
  }

  public IAuthenticationResult retrieveAuthenticationResult(String code) {
    return authTokensStorage.retrieve(code);
  }

  public IAuthenticationResult acquireAuthenticationResultSilently(String code) {
    UserDeviceCodeWrapper userDeviceCode = new UserDeviceCodeWrapper();
    userDeviceCode.setUserDeviceCode(code);

    IAuthenticationResult authenticationResult = null;
    IAuthenticationResult prevAuthenticationResult = authTokensStorage.retrieve(code);
    if (prevAuthenticationResult != null) {
      authenticationResult = startSilentlyPublicClientAuthentication(prevAuthenticationResult.account(), userDeviceCode);
    }
    return authenticationResult;
  }

  private void startPublicClientAuthentication(Consumer<DeviceCode> deviceCodeConsumer, UserDeviceCodeWrapper userDeviceCode) {
    log.info("Entering startPublicClientAuthentication.");

    publicClientAuthentication.initialize(appId, tenantId);

    IAuthenticationResult authenticationResult = publicClientAuthentication.getAuthenticationResult(appScopes, deviceCodeConsumer);

    if (authenticationResult != null) {
      authTokensStorage.save(userDeviceCode.getUserDeviceCode(), authenticationResult);
      log.info("authenticationResult.accessToken(): " + authenticationResult.accessToken());
      log.info("authenticationResult.scopes(): " + authenticationResult.scopes());
      log.info("authenticationResult.environment(): " + authenticationResult.environment());
      log.info("authenticationResult.idToken(): " + authenticationResult.idToken());
      log.info("authenticationResult.expiresOnDate(): " + authenticationResult.expiresOnDate());
    }

    log.info("Leaving startPublicClientAuthentication.");
  }

  private IAuthenticationResult startSilentlyPublicClientAuthentication(IAccount account, UserDeviceCodeWrapper userDeviceCode) {
    log.info("Entering startSilentlyPublicClientAuthentication.");

    publicClientAuthentication.initialize(appId, tenantId);

    IAuthenticationResult authenticationResult = publicClientAuthentication.getAuthenticationResult(appScopes, account);

    if (authenticationResult != null) {
      authTokensStorage.save(userDeviceCode.getUserDeviceCode(), authenticationResult);
      log.info("authenticationResult.accessToken(): " + authenticationResult.accessToken());
      log.info("authenticationResult.scopes(): " + authenticationResult.scopes());
      log.info("authenticationResult.environment(): " + authenticationResult.environment());
      log.info("authenticationResult.idToken(): " + authenticationResult.idToken());
      log.info("authenticationResult.expiresOnDate(): " + authenticationResult.expiresOnDate());
    }

    log.info("Leaving startSilentlyPublicClientAuthentication.");

    return authenticationResult;
  }
}
