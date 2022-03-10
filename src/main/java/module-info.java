module org.alienlabs.adaloveslace.impl {
  requires javafx.controls;
  requires javafx.graphics;
  requires org.slf4j;

  opens org.alienlabs.adaloveslace to javafx.graphics, javafx.controls;
  exports org.alienlabs.adaloveslace;
}
