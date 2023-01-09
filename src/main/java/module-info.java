module org.alienlabs.adaloveslace {
  // iText
  requires io;
  requires kernel;

  requires com.google.gson;
  requires jakarta.xml.bind;
  requires java.net.http;
  requires java.prefs;
  requires javafx.graphics;
  requires org.slf4j;
  requires static com.github.spotbugs.annotations;
  requires static javafx.controls;
  requires static javafx.swing;
  requires static layout;

  opens org.alienlabs.adaloveslace                  to javafx.controls, javafx.graphics, layout;
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
