package org.alienlabs.adaloveslace.integrationtest.persistence.support;

import javafx.scene.paint.Color;
import org.alienlabs.adaloveslace.domain.Diagram;
import org.alienlabs.adaloveslace.domain.Knot;
import org.alienlabs.adaloveslace.domain.Pattern;
import org.alienlabs.adaloveslace.domain.Step;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public final class DiagramSemanticAssertions {

    private static final double EPS = 1e-9;

    private DiagramSemanticAssertions() {
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

        Map<String, Pattern> apByName = new HashMap<>();
        for (Pattern p : ap) {
            apByName.put(p.getFilename(), p);
        }

        for (Pattern p : ep) {
            Pattern loaded = apByName.get(p.getFilename());
            assertNotNull(loaded, "missing pattern: " + p.getFilename());
            assertEquals(p.getCenterX(), loaded.getCenterX(), EPS);
            assertEquals(p.getCenterY(), loaded.getCenterY(), EPS);
            assertEquals(p.getWidth(), loaded.getWidth(), EPS);
            assertEquals(p.getHeight(), loaded.getHeight(), EPS);
        }
    }

    private static void assertSteps(Diagram expected, Diagram actual) {
        List<Step> es = new ArrayList<>(expected.getAllSteps());
        List<Step> as = new ArrayList<>(actual.getAllSteps());
        assertEquals(es.size(), as.size(), "step count mismatch");
        es.sort(Comparator.comparingInt(Step::getStepIndex));
        as.sort(Comparator.comparingInt(Step::getStepIndex));

        for (int i = 0; i < es.size(); i++) {
            Step e = es.get(i);
            Step a = as.get(i);
            assertEquals(e.getStepIndex(), a.getStepIndex(), "stepIndex mismatch");
            assertKnotLists(e.getDisplayedKnots(), a.getDisplayedKnots(), "displayed");
            assertKnotLists(e.getSelectedKnots(), a.getSelectedKnots(), "selected");
        }
    }

    private static void assertKnotLists(List<Knot> expected, List<Knot> actual, String kind) {
        assertEquals(expected.size(), actual.size(), kind + " knot count mismatch");
        List<Knot> ek = new ArrayList<>(expected);
        List<Knot> ak = new ArrayList<>(actual);

        Comparator<Knot> cmp = Comparator
                .comparingInt((Knot k) -> k.getPattern().isPresent() ? 0 : 1)
                .thenComparingDouble(Knot::getX)
                .thenComparingDouble(Knot::getY)
                .thenComparingInt(Knot::getRotationAngle)
                .thenComparingInt(Knot::getZoomFactor);

        ek.sort(cmp);
        ak.sort(cmp);
        for (int i = 0; i < ek.size(); i++) {
            assertKnot(ek.get(i), ak.get(i));
        }
    }

    private static void assertKnot(Knot expected, Knot actual) {
        assertEquals(expected.getX(), actual.getX(), EPS);
        assertEquals(expected.getY(), actual.getY(), EPS);
        assertEquals(expected.getRotationAngle(), actual.getRotationAngle());
        assertEquals(expected.getZoomFactor(), actual.getZoomFactor());
        assertEquals(expected.isVisible(), actual.isVisible());
        assertEquals(expected.isSelectable(), actual.isSelectable());
        assertEquals(expected.isFlippedHorizontally(), actual.isFlippedHorizontally());
        assertEquals(expected.isFlippedVertically(), actual.isFlippedVertically());

        assertEquals(expected.getPattern().isPresent(), actual.getPattern().isPresent());
        if (expected.getPattern().isPresent()) {
            assertEquals(expected.getPattern().get().getFilename(), actual.getPattern().get().getFilename());
        }

        assertEquals(expected.getText().isPresent(), actual.getText().isPresent());
        if (expected.getText().isPresent()) {
            assertEquals(expected.getText().get(), actual.getText().get());
        }

        if (expected.getPattern().isEmpty()) {
            String et = expected.getTypedText() == null ? null : expected.getTypedText().toString();
            String at = actual.getTypedText() == null ? null : actual.getTypedText().toString();
            assertEquals(et, at);
        }

        assertEquals(expected.getColor().isPresent(), actual.getColor().isPresent());
        if (expected.getColor().isPresent()) {
            assertColor(expected.getColor().get(), actual.getColor().get());
        }
    }

    private static void assertColor(Color expected, Color actual) {
        assertEquals(expected.getRed(), actual.getRed(), 1e-3);
        assertEquals(expected.getGreen(), actual.getGreen(), 1e-3);
        assertEquals(expected.getBlue(), actual.getBlue(), 1e-3);
        assertEquals(expected.getOpacity(), actual.getOpacity(), 1e-3);
    }
}

