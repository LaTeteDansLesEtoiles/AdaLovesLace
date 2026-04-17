package org.alienlabs.adaloveslace.unittest;

import org.alienlabs.adaloveslace.NewMain;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("unit")
class NewMainEntrypointTest {

  @Test
  void newMain_delegates_to_app_main_surface() throws Exception {
    Method main = NewMain.class.getMethod("main", String[].class);
    assertTrue(Modifier.isStatic(main.getModifiers()));
    Method appMain = org.alienlabs.adaloveslace.App.class.getMethod("main", String[].class);
    assertEquals(appMain.getReturnType(), main.getReturnType());
  }

  @Test
  void newMain_is_final_with_non_instantiable_constructor() throws Exception {
    assertTrue(Modifier.isFinal(NewMain.class.getModifiers()));
    Constructor<NewMain> ctor = NewMain.class.getDeclaredConstructor();
    assertTrue(Modifier.isPrivate(ctor.getModifiers()));
  }
}
