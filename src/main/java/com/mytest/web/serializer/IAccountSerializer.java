package com.mytest.web.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.microsoft.aad.msal4j.IAccount;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.jackson.JsonComponent;

@Slf4j
@JsonComponent
public class IAccountSerializer extends JsonSerializer<IAccount> {

  @Override
  public Class<IAccount> handledType() {
    return IAccount.class;
  }

  @Override
  public void serialize(IAccount value, JsonGenerator jgen,
      SerializerProvider serializers) throws IOException {
    if (value == null) {
      jgen.writeNull();
    } else {
      try {
        jgen.writeStartObject();
        jgen.writeStringField("username", value.username());
        jgen.writeStringField("environment", value.environment());
        jgen.writeStringField("homeAccountId", value.homeAccountId());
        jgen.writeEndObject();
      } catch (Exception ex) {
        log.error("Exception serializing AuthenticationResult", ex);
        jgen.writeNull();
      }
    }

  }
}
