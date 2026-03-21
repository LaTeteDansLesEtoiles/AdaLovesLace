package org.alienlabs.adaloveslace.unittest.persistence;

import javafx.scene.paint.Color;
import org.alienlabs.adaloveslace.domain.Diagram;
import org.alienlabs.adaloveslace.domain.Knot;
import org.alienlabs.adaloveslace.domain.Pattern;
import org.alienlabs.adaloveslace.domain.Step;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Semantic comparison helper for persistence tests.
 * <p>
 * It intentionally ignores unstable fields (like internal UUIDs, absolute filenames, transient UI nodes).
 */
public final class DiagramSemanticComparator {

    private static final double EPS = 1e-9;

    private DiagramSemanticComparator() {
        // Utility
    }

    public static void assertSemanticallyEquivalent(Diagram expected, Diagram actual) {
        assertNotNull(expected);
        assertNotNull(actual);

        assertEquals(expected.getCurrentStepIndex(), actual.getCurrentStepIndex(), "currentStepIndex mismatch");
        assertEquals(expected.getCurrentGridType(), actual.getCurrentGridType(), "currentGridType mismatch");

        assertPatterns(expected, actual);
        assertSteps(expected, actual);
    }

    private static void assertPatterns(Diagram expected, Diagram actual) {
        Set<Pattern> ep = expected.getPatterns();
        Set<Pattern> ap = actual.getPatterns();

        assertNotNull(ep);
        assertNotNull(ap);
        assertEquals(ep.size(), ap.size(), "pattern count mismatch");

        Map<String, Pattern> actualByFilename = new HashMap<>();
        for (Pattern p : ap) {
            actualByFilename.put(p.getFilename(), p);
        }

        for (Pattern p : ep) {
            Pattern loaded = actualByFilename.get(p.getFilename());
            assertNotNull(loaded, "missing pattern in loaded diagram: " + p.getFilename());
            assertEquals(p.getCenterX(), loaded.getCenterX(), EPS, "pattern centerX mismatch for " + p.getFilename());
            assertEquals(p.getCenterY(), loaded.getCenterY(), EPS, "pattern centerY mismatch for " + p.getFilename());
            assertEquals(p.getWidth(), loaded.getWidth(), EPS, "pattern width mismatch for " + p.getFilename());
            assertEquals(p.getHeight(), loaded.getHeight(), EPS, "pattern height mismatch for " + p.getFilename());
        }
    }

    private static void assertSteps(Diagram expected, Diagram actual) {
        List<Step> es = expected.getAllSteps();
        List<Step> as = actual.getAllSteps();

        assertNotNull(es);
        assertNotNull(as);
        assertEquals(es.size(), as.size(), "step count mismatch");

        List<Step> expectedSorted = new ArrayList<>(es);
        expectedSorted.sort(Comparator.comparingInt(s -> s.getStepIndex()));

        List<Step> actualSorted = new ArrayList<>(as);
        actualSorted.sort(Comparator.comparingInt(s -> s.getStepIndex()));

        for (int i = 0; i < expectedSorted.size(); i++) {
            Step e = expectedSorted.get(i);
            Step a = actualSorted.get(i);

            assertEquals(e.getStepIndex(), a.getStepIndex(), "stepIndex mismatch");
            assertKnotListsSemanticallyEqual(e.getDisplayedKnots(), a.getDisplayedKnots(), "displayedKnots");
            assertKnotListsSemanticallyEqual(e.getSelectedKnots(), a.getSelectedKnots(), "selectedKnots");
        }
    }

    private static void assertKnotListsSemanticallyEqual(List<Knot> expectedKnots, List<Knot> actualKnots, String kind) {
        assertNotNull(expectedKnots);
        assertNotNull(actualKnots);
        assertEquals(expectedKnots.size(), actualKnots.size(), kind + " knot count mismatch");

        List<Knot> expectedSorted = new ArrayList<>(expectedKnots);
        List<Knot> actualSorted = new ArrayList<>(actualKnots);

        Comparator<Knot> cmp = Comparator
                .comparingInt((Knot k) -> k.getPattern().isPresent() ? 0 : 1)
                .thenComparingDouble(Knot::getX)
                .thenComparingDouble(Knot::getY)
                .thenComparingInt(Knot::getRotationAngle)
                .thenComparingInt(Knot::getZoomFactor);

        expectedSorted.sort(cmp);
        actualSorted.sort(cmp);

        for (int i = 0; i < expectedSorted.size(); i++) {
            assertKnotSemanticallyEqual(expectedSorted.get(i), actualSorted.get(i));
        }
    }

    private static void assertKnotSemanticallyEqual(Knot expected, Knot actual) {
        assertNotNull(expected);
        assertNotNull(actual);

        assertEquals(expected.getX(), actual.getX(), EPS, "knot x mismatch");
        assertEquals(expected.getY(), actual.getY(), EPS, "knot y mismatch");
        assertEquals(expected.getRotationAngle(), actual.getRotationAngle(), "knot rotation mismatch");
        assertEquals(expected.getZoomFactor(), actual.getZoomFactor(), "knot zoom mismatch");

        assertEquals(expected.isVisible(), actual.isVisible(), "knot visible mismatch");
        assertEquals(expected.isSelectable(), actual.isSelectable(), "knot selectable mismatch");
        assertEquals(expected.isFlippedVertically(), actual.isFlippedVertically(), "knot flippedVertically mismatch");
        assertEquals(expected.isFlippedHorizontally(), actual.isFlippedHorizontally(), "knot flippedHorizontally mismatch");

        assertEquals(expected.getPattern().isPresent(), actual.getPattern().isPresent(), "knot pattern presence mismatch");
        if (expected.getPattern().isPresent()) {
            assertEquals(expected.getPattern().get().getFilename(), actual.getPattern().get().getFilename(), "knot pattern filename mismatch");
        }

        assertEquals(expected.getText().isPresent(), actual.getText().isPresent(), "knot text presence mismatch");
        if (expected.getText().isPresent()) {
            assertEquals(expected.getText().get(), actual.getText().get(), "knot text mismatch");
        }

        // typedText is only meaningful for text knots in this app.
        if (expected.getPattern().isEmpty()) {
            String et = expected.getTypedText() == null ? null : expected.getTypedText().toString();
            String at = actual.getTypedText() == null ? null : actual.getTypedText().toString();
            assertEquals(et, at, "knot typedText mismatch");
        }

        assertEquals(expected.getColor().isPresent(), actual.getColor().isPresent(), "knot color presence mismatch");
        if (expected.getColor().isPresent()) {
            assertColorEquals(expected.getColor().get(), actual.getColor().get());
        }
    }

    private static void assertColorEquals(Color expected, Color actual) {
        assertNotNull(expected);
        assertNotNull(actual);
        assertEquals(expected.getRed(), actual.getRed(), 1e-3, "color red mismatch");
        assertEquals(expected.getGreen(), actual.getGreen(), 1e-3, "color green mismatch");
        assertEquals(expected.getBlue(), actual.getBlue(), 1e-3, "color blue mismatch");
        assertEquals(expected.getOpacity(), actual.getOpacity(), 1e-3, "color opacity mismatch");
    }
}

