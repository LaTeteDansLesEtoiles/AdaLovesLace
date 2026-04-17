package org.alienlabs.adaloveslace.integrationtest.domain.enumeration;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import org.alienlabs.adaloveslace.domain.Diagram;
import org.alienlabs.adaloveslace.domain.enumeration.GridType;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.io.StringReader;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Verifies {@link GridType} survives a real JAXB marshal/unmarshal of {@link Diagram} (legacy XML path).
 */
@Tag("integration")
class GridTypeDiagramJaxbRoundTripIntegrationTest {

  @ParameterizedTest
  @EnumSource(GridType.class)
  void jaxb_round_trip_preserves_grid_type(GridType gridType) throws Exception {
    // Match {@link org.alienlabs.adaloveslace.persistence.LegacyXmlCompatibilityLoader}: single root type.
    JAXBContext ctx = JAXBContext.newInstance(Diagram.class);

    Diagram original = new Diagram();
    original.setName("jaxb-grid-" + gridType.name());
    original.setCurrentGridType(gridType);

    Marshaller marshaller = ctx.createMarshaller();
    StringWriter sw = new StringWriter();
    marshaller.marshal(original, sw);

    Unmarshaller unmarshaller = ctx.createUnmarshaller();
    Diagram copy = (Diagram) unmarshaller.unmarshal(new StringReader(sw.toString()));

    assertEquals(gridType, copy.getCurrentGridType());
    assertEquals(original.getName(), copy.getName());
  }
}
