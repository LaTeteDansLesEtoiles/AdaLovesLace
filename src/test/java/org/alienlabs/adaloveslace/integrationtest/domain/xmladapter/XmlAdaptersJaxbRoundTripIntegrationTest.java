package org.alienlabs.adaloveslace.integrationtest.domain.xmladapter;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("integration")
class XmlAdaptersJaxbRoundTripIntegrationTest {

  @Test
  void jaxb_marshal_unmarshal_preserves_optional_fields() throws Exception {
    JAXBContext ctx = JAXBContext.newInstance(XmlAdapterProbe.class, org.alienlabs.adaloveslace.domain.Pattern.class);
    XmlAdapterProbe original = XmlAdapterProbe.sample();

    Marshaller marshaller = ctx.createMarshaller();
    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
    StringWriter sw = new StringWriter();
    marshaller.marshal(original, sw);

    Unmarshaller unmarshaller = ctx.createUnmarshaller();
    XmlAdapterProbe copy = (XmlAdapterProbe) unmarshaller.unmarshal(new StringReader(sw.toString()));

    assertEquals(original.getColor(), copy.getColor());
    assertEquals(original.getText(), copy.getText());
    assertEquals(original.getPattern(), copy.getPattern());
  }
}
