package com.mytest.services;

import com.microsoft.aad.msal4j.IAuthenticationResult;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class OAuthTokensStorage {

  private Map<String, IAuthenticationResult> authenticationResultMap = new HashMap<>();

  public void save(String code, IAuthenticationResult authenticationResult) {
    authenticationResultMap.put(code, authenticationResult);
  }

  public IAuthenticationResult retrieve(String code) {
    return authenticationResultMap.get(code);
  }

}
