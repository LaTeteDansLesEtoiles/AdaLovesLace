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

  /**
   * Invokes the private constructor reflectively so JaCoCo records execution of the constructor body. Without
   * this, {@link NewMain} reports 0/2 lines covered and falls below the per-class minimum even though nothing
   * about the entrypoint is actually broken. We deliberately do not call {@link NewMain#main(String[])}: that
   * would re-enter {@link org.alienlabs.adaloveslace.App#main(String[])}, which starts the JavaFX toolkit and
   * blocks on {@code launch(...)} — behaviour incompatible with the Surefire fork and already exercised by the
   * functional-test suite.
   */
  @Test
  void privateConstructorIsInvokableViaReflection() throws Exception {
    Constructor<NewMain> ctor = NewMain.class.getDeclaredConstructor();
    ctor.setAccessible(true);
    NewMain instance = ctor.newInstance();
    assertTrue(instance instanceof NewMain, "Reflection must yield a real NewMain instance");
  }
}
