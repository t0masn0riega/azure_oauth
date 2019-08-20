package com.mytest.web.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.jackson.JsonComponent;

@Slf4j
@JsonComponent
public class IAuthenticationResultSerializer extends JsonSerializer<IAuthenticationResult> {

  @Override
  public Class<IAuthenticationResult> handledType() {
    return IAuthenticationResult.class;
  }

  @Override
  public void serialize(IAuthenticationResult value, JsonGenerator jgen,
      SerializerProvider serializers) throws IOException {
    if (value == null) {
      jgen.writeNull();
    } else {
      try {
        jgen.writeStartObject();
        jgen.writeStringField("accessToken", value.accessToken());
        jgen.writeStringField("scopes", value.scopes());
        jgen.writeStringField("idToken", value.idToken());
        jgen.writeStringField("expiresOnDate", value.expiresOnDate().toString());
        jgen.writeStringField("environment", value.environment());
        jgen.writeObjectField("account", value.account());
        jgen.writeEndObject();
      } catch (Exception ex) {
        log.error("Exception serializing AuthenticationResult", ex);
        jgen.writeNull();
      }
    }

  }
}
