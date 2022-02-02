module org.alienlabs.adaloveslace {
  requires javafx.controls;
  requires javafx.graphics;
  requires java.prefs;

  opens org.alienlabs.adaloveslace to javafx.graphics, javafx.controls;
}
