package com.mytest.web.controller;

import com.microsoft.aad.msal4j.DeviceCode;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.mytest.services.OAuthTokensHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

@RestController
@Slf4j
@RequestMapping("/api/v1/oauth")
public class OAuthActionsController {

  @Autowired
  OAuthTokensHandler oAuthTokensHandler;

  @PostMapping("/tokens")
  public DeferredResult<ResponseEntity<DeviceCode>> createOAuthTokens() {
    log.info("Entering - createOAuthTokens");
    DeferredResult<ResponseEntity<DeviceCode>> output = new DeferredResult<>();
    oAuthTokensHandler.initiateCodeFlow(output);
    log.info("Leaving - createOAuthTokens");
    return output;
  }

  @GetMapping("/tokens/{code}")
  public ResponseEntity<IAuthenticationResult> retrieveOAuthTokens(@PathVariable String code) {
    log.info("Entering - retrieveOAuthTokens");
    IAuthenticationResult authenticationResult = oAuthTokensHandler.retrieveAuthenticationResult(code);
    log.info("Leaving - retrieveOAuthTokens: " + authenticationResult);
    return new ResponseEntity(authenticationResult, HttpStatus.OK);
  }

  @PostMapping("/tokens/{code}")
  public ResponseEntity<IAuthenticationResult> createOAuthTokensSilently(@PathVariable String code) {
    log.info("Entering - retrieveOAuthTokensSilently");
    IAuthenticationResult authenticationResult = oAuthTokensHandler.acquireAuthenticationResultSilently(code);
    log.info("Leaving - retrieveOAuthTokensSilently: " + authenticationResult);
    return new ResponseEntity(authenticationResult, HttpStatus.OK);
  }
}
