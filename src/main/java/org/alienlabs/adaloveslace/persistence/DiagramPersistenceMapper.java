package org.alienlabs.adaloveslace.persistence;

import javafx.scene.paint.Color;
import org.alienlabs.adaloveslace.domain.Diagram;
import org.alienlabs.adaloveslace.domain.Knot;
import org.alienlabs.adaloveslace.domain.Pattern;
import org.alienlabs.adaloveslace.domain.Step;
import org.alienlabs.adaloveslace.domain.enumeration.GridType;
import org.alienlabs.adaloveslace.persistence.protobuf.DiagramDescriptor;
import org.alienlabs.adaloveslace.persistence.protobuf.KnotDescriptor;
import org.alienlabs.adaloveslace.persistence.protobuf.PatternDescriptor;
import org.alienlabs.adaloveslace.persistence.protobuf.StepDescriptor;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Maps between domain model and protobuf persistence model.
 */
public final class DiagramPersistenceMapper {

    private static final int CURRENT_SCHEMA_VERSION = 1;

    private DiagramPersistenceMapper() {
        // Utility
    }

    public static DiagramDescriptor toProto(Diagram diagram) {
        DiagramDescriptor.Builder builder = DiagramDescriptor.newBuilder()
                .setSchemaVersion(CURRENT_SCHEMA_VERSION)
                .setName(Optional.ofNullable(diagram.getName()).orElse(""))
                .setGridType(toProtoGridType(diagram.getCurrentGridType()))
                .setCurrentStepIndex(Optional.ofNullable(diagram.getCurrentStepIndex()).orElse(0));

        // Patterns (sorted for deterministic output)
        for (Pattern p : diagram.getPatterns().stream()
                .sorted(Comparator.comparing(Pattern::getFilename))
                .toList()) {
            PatternDescriptor pd = PatternDescriptor.newBuilder()
                    .setFilename(Optional.ofNullable(p.getFilename()).orElse(""))
                    .setCenterX(p.getCenterX())
                    .setCenterY(p.getCenterY())
                    .setWidth(p.getWidth())
                    .setHeight(p.getHeight())
                    .build();
            builder.addPatterns(pd);
        }

        // Steps
        for (Step step : diagram.getAllSteps()) {
            StepDescriptor.Builder stepBuilder = StepDescriptor.newBuilder()
                    .setStepIndex(Optional.ofNullable(step.getStepIndex()).orElse(0));

            for (Knot k : step.getDisplayedKnots()) {
                stepBuilder.addDisplayedKnots(toProtoKnot(k));
            }
            for (Knot k : step.getSelectedKnots()) {
                stepBuilder.addSelectedKnots(toProtoKnot(k));
            }
            builder.addSteps(stepBuilder.build());
        }

        return builder.build();
    }

    private static KnotDescriptor toProtoKnot(Knot knot) {
        KnotDescriptor.Builder builder = KnotDescriptor.newBuilder()
                .setUuid(knot.getUuid().toString())
                .setX(knot.getX())
                .setY(knot.getY())
                .setRotationAngle(knot.getRotationAngle())
                .setZoomFactor(knot.getZoomFactor())
                .setVisible(knot.isVisible())
                .setSelectable(knot.isSelectable())
                .setFlippedVertically(knot.isFlippedVertically())
                .setFlippedHorizontally(knot.isFlippedHorizontally());

        knot.getPattern().ifPresent(p -> builder.setPatternFilename(
                Optional.ofNullable(p.getFilename()).orElse("")));

        knot.getText().ifPresent(builder::setText);

        knot.getColor().ifPresent(c -> {
            int argb = colorToArgb(c);
            builder.setArgbColor(argb);
            builder.setHasColor(true);
        });

        if (knot.getTextId() != null) {
            builder.setTextId(knot.getTextId().toString());
        }
        if (knot.getTypedText() != null) {
            builder.setTypedText(knot.getTypedText().toString());
        }
        return builder.build();
    }

    public static Diagram fromProto(DiagramDescriptor descriptor, Diagram emptyDiagramTemplate) {
        if (descriptor.getSchemaVersion() != CURRENT_SCHEMA_VERSION) {
            throw new UnsupportedDescriptorVersionException(
                    "Unsupported descriptor schema version: " + descriptor.getSchemaVersion());
        }

        // Start from a fresh Diagram instance, preserving App wiring from the template.
        Diagram diagram = new Diagram(emptyDiagramTemplate.getApp());
        diagram.setName(descriptor.getName());
        diagram.setCurrentGridType(fromProtoGridType(descriptor.getGridType()));

        // Patterns
        Map<String, Pattern> patternsByFilename = new HashMap<>();
        for (PatternDescriptor pd : descriptor.getPatternsList()) {
            Pattern p = new Pattern();
            p.setFilename(pd.getFilename());
            p.setCenterX(pd.getCenterX());
            p.setCenterY(pd.getCenterY());
            p.setWidth(pd.getWidth());
            p.setHeight(pd.getHeight());
            // Rehydrate absolute filename so existing rendering/resource code can work.
            p.setAbsoluteFilename(
                    org.alienlabs.adaloveslace.util.FileUtil.APP_FOLDER_IN_USER_HOME +
                            org.alienlabs.adaloveslace.App.PATTERNS_DIRECTORY_NAME +
                            java.io.File.separator +
                            p.getFilename()
            );
            patternsByFilename.put(p.getFilename(), p);
            diagram.addPattern(p);
        }

        List<Step> steps = new ArrayList<>();
        for (StepDescriptor sd : descriptor.getStepsList()) {
            Step step = new Step();
            step.setStepIndex(sd.getStepIndex());

            step.setDisplayedKnots(sd.getDisplayedKnotsList().stream()
                    .map(kd -> fromProtoKnot(kd, patternsByFilename))
                    .collect(Collectors.toCollection(ArrayList::new)));

            step.setSelectedKnots(sd.getSelectedKnotsList().stream()
                    .map(kd -> fromProtoKnot(kd, patternsByFilename))
                    .collect(Collectors.toCollection(ArrayList::new)));

            steps.add(step);
        }
        diagram.setAllSteps(steps);

        diagram.setCurrentStepIndex(descriptor.getCurrentStepIndex());
        diagram.setCurrentPattern(diagram.getPatterns().stream()
                .sorted(Comparator.comparing(Pattern::getFilename))
                .findFirst()
                .orElse(null));

        return diagram;
    }

    private static Knot fromProtoKnot(KnotDescriptor kd, Map<String, Pattern> patternsByFilename) {
        Knot knot = new Knot();
        knot.setX(kd.getX());
        knot.setY(kd.getY());
        knot.setRotationAngle(kd.getRotationAngle());
        knot.setZoomFactor(kd.getZoomFactor());
        knot.setVisible(kd.getVisible());
        knot.setSelectable(kd.getSelectable());
        knot.setFlippedVertically(kd.getFlippedVertically());
        knot.setFlippedHorizontally(kd.getFlippedHorizontally());

        if (!kd.getPatternFilename().isEmpty()) {
            Pattern pattern = patternsByFilename.get(kd.getPatternFilename());
            if (pattern != null) {
                knot.setPattern(Optional.of(pattern));
            }
        }

        if (!kd.getText().isEmpty()) {
            knot.setText(Optional.of(kd.getText()));
        }

        if (kd.getHasColor()) {
            knot.setColor(Optional.of(argbToColor(kd.getArgbColor())));
        }

        if (!kd.getTextId().isEmpty()) {
            knot.setTextId(UUID.fromString(kd.getTextId()));
        }

        if (!kd.getTypedText().isEmpty()) {
            knot.setTypedText(new StringBuilder(kd.getTypedText()));
        }

        return knot;
    }

    private static org.alienlabs.adaloveslace.persistence.protobuf.GridType toProtoGridType(GridType domainGridType) {
        if (domainGridType == null) {
            return org.alienlabs.adaloveslace.persistence.protobuf.GridType.GRID_TYPE_UNSPECIFIED;
        }
        return switch (domainGridType) {
            case STAGGERED -> org.alienlabs.adaloveslace.persistence.protobuf.GridType.STAGGERED;
            case LATTICE -> org.alienlabs.adaloveslace.persistence.protobuf.GridType.LATTICE;
            case POLAR -> org.alienlabs.adaloveslace.persistence.protobuf.GridType.POLAR;
            case CRISS_CROSS -> org.alienlabs.adaloveslace.persistence.protobuf.GridType.CRISS_CROSS;
            case HIDDEN -> org.alienlabs.adaloveslace.persistence.protobuf.GridType.HIDDEN;
        };
    }

    private static GridType fromProtoGridType(org.alienlabs.adaloveslace.persistence.protobuf.GridType protoGridType) {
        return switch (protoGridType) {
            case STAGGERED -> GridType.STAGGERED;
            case LATTICE -> GridType.LATTICE;
            case POLAR -> GridType.POLAR;
            case CRISS_CROSS -> GridType.CRISS_CROSS;
            case HIDDEN -> GridType.HIDDEN;
            case GRID_TYPE_UNSPECIFIED, UNRECOGNIZED -> GridType.CRISS_CROSS;
        };
    }

    private static int colorToArgb(Color color) {
        int a = (int) Math.round(color.getOpacity() * 255.0);
        int r = (int) Math.round(color.getRed() * 255.0);
        int g = (int) Math.round(color.getGreen() * 255.0);
        int b = (int) Math.round(color.getBlue() * 255.0);
        return (a & 0xFF) << 24 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | (b & 0xFF);
    }

    private static Color argbToColor(int argb) {
        int a = (argb >> 24) & 0xFF;
        int r = (argb >> 16) & 0xFF;
        int g = (argb >> 8) & 0xFF;
        int b = argb & 0xFF;
        return new Color(r / 255.0, g / 255.0, b / 255.0, a / 255.0);
    }

    public static int getCurrentSchemaVersion() {
        return CURRENT_SCHEMA_VERSION;
    }
}

