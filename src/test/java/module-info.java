module org.alienlabs.adaloveslace.test {
  requires org.alienlabs.adaloveslace.impl;

  opens org.alienlabs.adaloveslace.test       to org.junit.platform.commons;
  opens org.alienlabs.adaloveslace.test.view  to org.junit.platform.commons, org.testfx.junit5;

  requires javafx.controls;
  requires javafx.graphics;
  requires javafx.swing;

  requires org.slf4j;
  requires org.testfx;
  requires org.testfx.junit5;
  requires org.hamcrest;

  requires transitive org.assertj.core;
  requires transitive org.junit.jupiter.engine;
  requires transitive org.junit.jupiter.api;
}
