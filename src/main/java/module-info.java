module org.alienlabs.adaloveslace.impl {
  requires javafx.controls;
  requires javafx.graphics;
  requires java.prefs;

  opens org.alienlabs.adaloveslace to javafx.graphics, javafx.controls;
  exports org.alienlabs.adaloveslace;
}
