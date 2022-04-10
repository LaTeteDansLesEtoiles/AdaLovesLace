module org.alienlabs.adaloveslace.impl {
  requires com.github.spotbugs.annotations;
  requires javafx.controls;
  requires javafx.graphics;
  requires org.slf4j;
  requires jakarta.xml.bind;

  opens org.alienlabs.adaloveslace to javafx.graphics, javafx.controls;
  exports org.alienlabs.adaloveslace;
  exports org.alienlabs.adaloveslace.view;
  exports org.alienlabs.adaloveslace.business.model to org.alienlabs.adaloveslace.test;
  exports org.alienlabs.adaloveslace.util;
}
