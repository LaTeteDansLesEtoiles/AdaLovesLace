module org.alienlabs.adaloveslace.test {
  requires org.alienlabs.adaloveslace;

  opens org.alienlabs.adaloveslace.functionaltest                                       to org.junit.platform.commons, org.junit.jupiter.params, testfx.junit5, javafx.graphics;
  opens org.alienlabs.adaloveslace.functionaltest.business.model                        to org.junit.platform.commons, testfx.junit5;
  opens org.alienlabs.adaloveslace.functionaltest.util                                  to org.junit.platform.commons, org.junit.jupiter.params, testfx.junit5;
  opens org.alienlabs.adaloveslace.functionaltest.view.component.button.geometrywindow  to org.junit.platform.commons, org.junit.jupiter.params, testfx.junit5;
  opens org.alienlabs.adaloveslace.functionaltest.view.component.button.toolboxwindow   to org.junit.platform.commons, org.junit.jupiter.params, testfx.junit5;
  opens org.alienlabs.adaloveslace.functionaltest.view.component.spinner                to org.junit.platform.commons, org.junit.jupiter.params, testfx.junit5;
  opens org.alienlabs.adaloveslace.functionaltest.view.window                           to org.junit.platform.commons, org.junit.jupiter.params, testfx.junit5;
  opens org.alienlabs.adaloveslace.unittest.business.model;
  opens org.alienlabs.adaloveslace.unittest.util;
  opens org.alienlabs.adaloveslace.unittest.view.component                              to org.junit.platform.commons, org.junit.jupiter.params, testfx.junit5;

  requires javafx.controls;
  requires javafx.graphics;
  requires javafx.swing;

  requires jakarta.xml.bind;
  requires org.hamcrest;
  requires org.mockito;
  requires org.slf4j;

  requires transitive org.assertj.core;
  requires transitive org.junit.jupiter.api;
  requires transitive org.junit.jupiter.engine;
  requires org.junit.jupiter.params;
  requires testfx.core;
  requires testfx.junit5;
}
