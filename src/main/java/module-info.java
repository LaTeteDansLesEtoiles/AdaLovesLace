module org.alienlabs.adaloveslace.impl {
  requires javafx.controls;
  requires javafx.graphics;
  requires jakarta.xml.bind;
  requires java.prefs;
  requires org.slf4j;
  requires static com.github.spotbugs.annotations;

  opens org.alienlabs.adaloveslace                  to javafx.graphics, javafx.controls;
  opens org.alienlabs.adaloveslace.business.model   to jakarta.xml.bind;

  exports org.alienlabs.adaloveslace;
  exports org.alienlabs.adaloveslace.business.model;
  exports org.alienlabs.adaloveslace.util;
  exports org.alienlabs.adaloveslace.view.window;
  exports org.alienlabs.adaloveslace.view.component;
  exports org.alienlabs.adaloveslace.view.component.button.geometrywindow;
  exports org.alienlabs.adaloveslace.view.component.button.toolboxwindow;
}
