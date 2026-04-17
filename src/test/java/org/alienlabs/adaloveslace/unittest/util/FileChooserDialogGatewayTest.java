package org.alienlabs.adaloveslace.unittest.util;

import org.alienlabs.adaloveslace.util.FileChooserDialogGateway;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Tag("unit")
class FileChooserDialogGatewayTest {

  @Test
  void java_fx_default_gateway_is_non_null() {
    assertNotNull(FileChooserDialogGateway.javaFxDefault());
  }
}
