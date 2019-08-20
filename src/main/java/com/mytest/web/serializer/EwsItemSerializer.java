package com.mytest.web.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import microsoft.exchange.webservices.data.core.service.item.Item;
import org.springframework.boot.jackson.JsonComponent;

@Slf4j
@JsonComponent
public class EwsItemSerializer extends JsonSerializer<Item> {

  @Override
  public Class<Item> handledType() {
    return Item.class;
  }

  @Override
  public void serialize(Item value, JsonGenerator jgen, SerializerProvider serializers)
      throws IOException {

    if (value == null) {
      jgen.writeNull();
    } else {
      try {
        jgen.writeStartObject();
        jgen.writeStringField("id", value.getId().toString());
        jgen.writeStringField("subject", value.getSubject());
        jgen.writeEndObject();
      } catch (Exception ex) {
        log.error("Exception serializing Item", ex);
        jgen.writeNull();
      }
    }
  }
}
