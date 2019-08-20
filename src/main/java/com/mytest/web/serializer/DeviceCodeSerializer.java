package com.mytest.web.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.microsoft.aad.msal4j.DeviceCode;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.jackson.JsonComponent;

@Slf4j
@JsonComponent
public class DeviceCodeSerializer extends JsonSerializer<DeviceCode> {

  @Override
  public Class<DeviceCode> handledType() {
    return DeviceCode.class;
  }

  @Override
  public void serialize(DeviceCode value, JsonGenerator jgen, SerializerProvider serializers)
      throws IOException {
    if (value == null) {
      jgen.writeNull();
    } else {
      try {
        jgen.writeStartObject();
        jgen.writeStringField("userCode", value.userCode());
        jgen.writeStringField("deviceCode", value.deviceCode());
        jgen.writeStringField("verificationUri", value.verificationUri());
        jgen.writeNumberField("expiresIn", value.expiresIn());
        jgen.writeNumberField("interval", value.interval());
        jgen.writeStringField("message", value.message());
        jgen.writeEndObject();
      } catch (Exception ex) {
        log.error("Exception serializing Item", ex);
        jgen.writeNull();
      }
    }
  }
}
