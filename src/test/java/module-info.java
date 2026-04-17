module org.alienlabs.adaloveslace.test {
  requires org.alienlabs.adaloveslace;

  opens org.alienlabs.adaloveslace.functionaltest                                       to org.junit.platform.commons, org.junit.jupiter.params, javafx.graphics;
  opens org.alienlabs.adaloveslace.functionaltest.domain                                to org.junit.platform.commons, org.testfx.junit5;
  opens org.alienlabs.adaloveslace.functionaltest.util                                  to org.junit.platform.commons, org.junit.jupiter.params, org.testfx.junit5;
  opens org.alienlabs.adaloveslace.functionaltest.view.component.button.geometrywindow     to org.junit.platform.commons, org.junit.jupiter.params, org.testfx.junit5;
  opens org.alienlabs.adaloveslace.functionaltest.view.component.button.geometrywindow.move to org.junit.platform.commons, org.junit.jupiter.params, org.testfx.junit5;
  opens org.alienlabs.adaloveslace.functionaltest.view.component.button.statewindow         to org.junit.platform.commons, org.junit.jupiter.params, org.testfx.junit5;
  opens org.alienlabs.adaloveslace.functionaltest.view.component.button.toolboxwindow   to org.junit.platform.commons, org.junit.jupiter.params, org.testfx.junit5;
  opens org.alienlabs.adaloveslace.functionaltest.view.component.button.toolboxwindow.grid to org.junit.platform.commons, org.junit.jupiter.params, org.testfx.junit5;
  opens org.alienlabs.adaloveslace.functionaltest.view.component.spinner                to org.junit.platform.commons, org.junit.jupiter.params, org.testfx.junit5;
  opens org.alienlabs.adaloveslace.functionaltest.view.component                        to org.junit.platform.commons, org.testfx.junit5;
  opens org.alienlabs.adaloveslace.functionaltest.view.window                           to org.junit.platform.commons, org.junit.jupiter.params, org.testfx.junit5;
  opens org.alienlabs.adaloveslace.unittest                                    to org.junit.platform.commons, org.junit.jupiter.params, org.testfx.junit5;
  opens org.alienlabs.adaloveslace.unittest.domain;
  opens org.alienlabs.adaloveslace.unittest.util;
  opens org.alienlabs.adaloveslace.unittest.view.component                              to org.junit.platform.commons, org.junit.jupiter.params, org.testfx.junit5;
  opens org.alienlabs.adaloveslace.unittest.view.component.button.geometrywindow.move   to org.junit.platform.commons, org.junit.jupiter.params, org.testfx.junit5;
  opens org.alienlabs.adaloveslace.unittest.view.component.button.statewindow           to org.junit.platform.commons, org.junit.jupiter.params, org.testfx.junit5;
  opens org.alienlabs.adaloveslace.unittest.view.component.button.toolboxwindow         to org.junit.platform.commons, org.junit.jupiter.params, org.testfx.junit5;
  opens org.alienlabs.adaloveslace.unittest.view.component.button.toolboxwindow.grid    to org.junit.platform.commons, org.junit.jupiter.params, org.testfx.junit5;
  opens org.alienlabs.adaloveslace.unittest.view.window.event                           to org.junit.platform.commons, org.testfx.junit5;
  opens org.alienlabs.adaloveslace.unittest.persistence                                 to org.junit.platform.commons, org.junit.jupiter.params, org.testfx.junit5;
  opens org.alienlabs.adaloveslace.integrationtest.app                               to org.junit.platform.commons, org.junit.jupiter.params, org.testfx.junit5;
  opens org.alienlabs.adaloveslace.integrationtest.persistence                          to org.junit.platform.commons, org.junit.jupiter.params, org.testfx.junit5;
  opens org.alienlabs.adaloveslace.integrationtest.util                                 to org.junit.platform.commons, org.junit.jupiter.params, org.testfx.junit5;
  opens org.alienlabs.adaloveslace.integrationtest.view.component.button.geometrywindow.move to org.junit.platform.commons, org.junit.jupiter.params, org.testfx.junit5;
  opens org.alienlabs.adaloveslace.integrationtest.view.component.button.statewindow        to org.junit.platform.commons, org.junit.jupiter.params, org.testfx.junit5;
  opens org.alienlabs.adaloveslace.integrationtest.view.component.button.toolboxwindow     to org.junit.platform.commons, org.junit.jupiter.params, org.testfx.junit5;
  opens org.alienlabs.adaloveslace.integrationtest.view.component.button.toolboxwindow.grid to org.junit.platform.commons, org.junit.jupiter.params, org.testfx.junit5;

  requires java.desktop;
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
  requires org.testfx;
  requires org.testfx.junit5;
}
