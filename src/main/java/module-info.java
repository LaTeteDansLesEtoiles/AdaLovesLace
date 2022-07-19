module org.alienlabs.adaloveslace.impl {
  // iText
  requires io;
  requires kernel;
  requires layout;

  requires com.google.gson;
  requires java.net.http;
  requires javafx.controls;
  requires javafx.graphics;
  requires javafx.swing;
  requires jakarta.xml.bind;
  requires java.prefs;
  requires org.slf4j;
  requires static com.github.spotbugs.annotations;

  opens org.alienlabs.adaloveslace                  to javafx.graphics, javafx.controls;
  opens org.alienlabs.adaloveslace.business.model   to com.google.gson, jakarta.xml.bind;

  exports org.alienlabs.adaloveslace;
  exports org.alienlabs.adaloveslace.business.model;
  exports org.alienlabs.adaloveslace.util;
  exports org.alienlabs.adaloveslace.view.window;
  exports org.alienlabs.adaloveslace.view.component;
  exports org.alienlabs.adaloveslace.view.component.button.geometrywindow;
  exports org.alienlabs.adaloveslace.view.component.button.toolboxwindow;
  exports org.alienlabs.adaloveslace.view.component.spinner;
}
