module org.alienlabs.adaloveslace.test {
  requires org.alienlabs.adaloveslace;

  opens org.alienlabs.adaloveslace.functionaltest                                       to org.junit.platform.commons, org.junit.jupiter.params, org.testfx.junit5;
  opens org.alienlabs.adaloveslace.functionaltest.view.component.button.geometrywindow  to org.junit.platform.commons, org.junit.jupiter.params, org.testfx.junit5;
  opens org.alienlabs.adaloveslace.functionaltest.view.component.button.toolboxwindow   to org.junit.platform.commons, org.junit.jupiter.params, org.testfx.junit5;
  opens org.alienlabs.adaloveslace.functionaltest.view.component.spinner                to org.junit.platform.commons, org.junit.jupiter.params, org.testfx.junit5;
  opens org.alienlabs.adaloveslace.functionaltest.view.window                           to org.junit.platform.commons, org.junit.jupiter.params, org.testfx.junit5;
  opens org.alienlabs.adaloveslace.unittest.business.model;
  opens org.alienlabs.adaloveslace.unittest.util                                        to org.junit.platform.commons, org.junit.jupiter.params, org.testfx.junit5;
  opens org.alienlabs.adaloveslace.unittest.view.component                              to org.junit.platform.commons, org.junit.jupiter.params, org.testfx.junit5;

  requires javafx.controls;
  requires javafx.graphics;
  requires javafx.swing;

  requires jakarta.xml.bind;
  requires org.hamcrest;
  requires org.mockito;
  requires org.slf4j;
  requires org.testfx;
  requires org.testfx.junit5;

  requires transitive org.assertj.core;
  requires transitive org.junit.jupiter.api;
  requires transitive org.junit.jupiter.engine;
  requires org.junit.jupiter.params;

  exports org.alienlabs.adaloveslace.functionaltest;
  exports org.alienlabs.adaloveslace.functionaltest.view.component.button.geometrywindow;
  exports org.alienlabs.adaloveslace.unittest.business.model;
  exports org.alienlabs.adaloveslace.unittest.util;
  exports org.alienlabs.adaloveslace.unittest.view.component;
}
