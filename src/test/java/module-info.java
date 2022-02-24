open module org.alienlabs.adaloveslace.test {
  requires org.alienlabs.adaloveslace.impl;

  requires javafx.controls;
  requires javafx.graphics;
  requires java.prefs;

  requires transitive org.junit.jupiter.engine;
  requires transitive org.junit.jupiter.api;
  requires org.testfx;
  requires org.testfx.junit5;

}
