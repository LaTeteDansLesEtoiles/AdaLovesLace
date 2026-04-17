module org.alienlabs.adaloveslace {
  // iText
  requires io;
  requires kernel;

  requires com.google.gson;
  requires jakarta.xml.bind;
  requires java.desktop;
  requires java.net.http;
  requires java.prefs;
  requires transitive javafx.graphics;
  requires transitive javafx.base;
  requires javafx.swing;
  requires org.slf4j;
  requires static com.github.spotbugs.annotations;
  requires javafx.controls;
  requires static layout;
  requires com.google.protobuf;
  requires com.google.protobuf.util;

  opens org.alienlabs.adaloveslace          to javafx.controls, javafx.graphics, layout;
  opens org.alienlabs.adaloveslace.domain   to com.google.gson, jakarta.xml.bind;
  opens org.alienlabs.adaloveslace.util;

  exports org.alienlabs.adaloveslace;
  exports org.alienlabs.adaloveslace.domain;
  exports org.alienlabs.adaloveslace.util;
  exports org.alienlabs.adaloveslace.view.window;
  exports org.alienlabs.adaloveslace.view.component;
  exports org.alienlabs.adaloveslace.view.component.button.geometrywindow;
  exports org.alienlabs.adaloveslace.view.component.button.geometrywindow.move;
  exports org.alienlabs.adaloveslace.view.component.button.statewindow;
  exports org.alienlabs.adaloveslace.view.component.button.toolboxwindow;
  exports org.alienlabs.adaloveslace.view.component.spinner;
  exports org.alienlabs.adaloveslace.domain.enumeration;
  opens org.alienlabs.adaloveslace.domain.enumeration to com.google.gson, jakarta.xml.bind;
  exports org.alienlabs.adaloveslace.domain.xmladapter;
  opens org.alienlabs.adaloveslace.domain.xmladapter  to com.google.gson, jakarta.xml.bind;
  exports org.alienlabs.adaloveslace.domain.dto;
  opens org.alienlabs.adaloveslace.domain.dto         to com.google.gson, jakarta.xml.bind;
  opens org.alienlabs.adaloveslace.persistence        to com.google.protobuf;
  exports org.alienlabs.adaloveslace.view.component.button.toolboxwindow.file;
  exports org.alienlabs.adaloveslace.view.component.button.toolboxwindow.grid;
  exports org.alienlabs.adaloveslace.view.component.grid;
  exports org.alienlabs.adaloveslace.view.component.grid.gridstrategy;
  exports org.alienlabs.adaloveslace.view.component.grid.gridstrategy.concretegridstrategy;
  exports org.alienlabs.adaloveslace.view.window.event;
  exports org.alienlabs.adaloveslace.persistence;
  opens org.alienlabs.adaloveslace.view.window.event;
  exports org.alienlabs.adaloveslace.persistence.protobuf;
}
