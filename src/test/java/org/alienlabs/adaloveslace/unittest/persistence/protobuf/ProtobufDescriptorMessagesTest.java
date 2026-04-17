package org.alienlabs.adaloveslace.unittest.persistence.protobuf;

import com.google.protobuf.InvalidProtocolBufferException;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.domain.Diagram;
import org.alienlabs.adaloveslace.domain.Knot;
import org.alienlabs.adaloveslace.domain.Pattern;
import org.alienlabs.adaloveslace.domain.Step;
import org.alienlabs.adaloveslace.domain.enumeration.GridType;
import org.alienlabs.adaloveslace.persistence.DiagramPersistenceMapper;
import org.alienlabs.adaloveslace.persistence.LaceArchiveException;
import org.alienlabs.adaloveslace.persistence.protobuf.DiagramDescriptor;
import org.alienlabs.adaloveslace.persistence.protobuf.KnotDescriptor;
import org.alienlabs.adaloveslace.persistence.protobuf.PatternDescriptor;
import org.alienlabs.adaloveslace.persistence.protobuf.StepDescriptor;
import org.alienlabs.adaloveslace.unittest.persistence.DiagramSemanticComparator;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("unit")
class ProtobufDescriptorMessagesTest {

  @Test
  void diagramDescriptor_minimal_wireRoundTrip() throws Exception {
    DiagramDescriptor original = DiagramDescriptor.newBuilder()
        .setSchemaVersion(1)
        .setName("")
        .setGridType(org.alienlabs.adaloveslace.persistence.protobuf.GridType.LATTICE)
        .setCurrentStepIndex(0)
        .build();

    DiagramDescriptor parsed = DiagramDescriptor.parseFrom(original.toByteArray());
    assertEquals(original, parsed);
  }

  @Test
  void knotDescriptor_withOptionals_wireRoundTrip() throws Exception {
    KnotDescriptor knot = KnotDescriptor.newBuilder()
        .setUuid("11111111-1111-1111-1111-111111111111")
        .setX(3.25)
        .setY(-1.0)
        .setRotationAngle(90)
        .setZoomFactor(4)
        .setPatternFilename("snow.png")
        .setText("hello")
        .setArgbColor(0xFF010203)
        .setHasColor(true)
        .setVisible(false)
        .setSelectable(true)
        .setFlippedVertically(true)
        .setFlippedHorizontally(false)
        .setTextId("22222222-2222-2222-2222-222222222222")
        .setTypedText("typed-content")
        .build();

    StepDescriptor step = StepDescriptor.newBuilder()
        .setStepIndex(7)
        .addDisplayedKnots(knot)
        .build();

    PatternDescriptor pattern = PatternDescriptor.newBuilder()
        .setFilename("snow.png")
        .setCenterX(1.0)
        .setCenterY(2.0)
        .setWidth(10.0)
        .setHeight(12.0)
        .build();

    DiagramDescriptor original = DiagramDescriptor.newBuilder()
        .setSchemaVersion(1)
        .setName("wire-test")
        .setGridType(org.alienlabs.adaloveslace.persistence.protobuf.GridType.POLAR)
        .setCurrentStepIndex(3)
        .addPatterns(pattern)
        .addSteps(step)
        .build();

    DiagramDescriptor parsed = DiagramDescriptor.parseFrom(original.toByteArray());
    assertEquals(original, parsed);
  }

  @Test
  void eachProtoGridType_roundTripsThroughWireFormat() throws Exception {
    for (org.alienlabs.adaloveslace.persistence.protobuf.GridType gt
        : org.alienlabs.adaloveslace.persistence.protobuf.GridType.values()) {
      if (gt == org.alienlabs.adaloveslace.persistence.protobuf.GridType.UNRECOGNIZED) {
        continue;
      }
      DiagramDescriptor original = DiagramDescriptor.newBuilder()
          .setSchemaVersion(1)
          .setGridType(gt)
          .build();
      DiagramDescriptor parsed = DiagramDescriptor.parseFrom(original.toByteArray());
      assertEquals(gt, parsed.getGridType(), "grid type " + gt);
    }
  }

  @Test
  void parseFrom_invalidBytes_throws() {
    InvalidProtocolBufferException ex = assertThrows(InvalidProtocolBufferException.class,
        () -> DiagramDescriptor.parseFrom(new byte[]{(byte) 0xFF, (byte) 0xFF}));
    assertTrue(ex.getMessage() != null && !ex.getMessage().isEmpty());
  }

  @Test
  void diagramPersistenceMapper_reportsCurrentSchemaVersion() {
    assertEquals(1, DiagramPersistenceMapper.getCurrentSchemaVersion());
  }

  @Test
  void diagramPersistenceMapper_emptyDiagram_semanticRoundTrip() {
    App app = new App();
    app.setMovablePane(new Pane());
    Diagram diagram = new Diagram(app);
    diagram.setName("");
    diagram.setCurrentGridType(GridType.HIDDEN);
    diagram.getPatterns().clear();
    diagram.setAllSteps(List.of());
    diagram.setCurrentStepIndex(0);

    DiagramDescriptor proto = DiagramPersistenceMapper.toProto(diagram);
    Diagram restored = DiagramPersistenceMapper.fromProto(proto, diagram);
    DiagramSemanticComparator.assertSemanticallyEquivalent(diagram, restored);
  }

  @Test
  void diagramPersistenceMapper_singlePatternAndKnot_semanticRoundTrip() {
    App app = new App();
    app.setMovablePane(new Pane());
    Diagram diagram = new Diagram(app);
    diagram.setName("proto-mapper");
    diagram.setCurrentGridType(GridType.STAGGERED);
    diagram.setCurrentStepIndex(1);

    Pattern pattern = new Pattern();
    pattern.setFilename("f.jpg");
    pattern.setAbsoluteFilename("/tmp/fake-patterns/f.jpg");
    pattern.setCenterX(0.5);
    pattern.setCenterY(1.5);
    pattern.setWidth(20.0);
    pattern.setHeight(22.0);

    Knot knot = new Knot(
        5.0,
        6.0,
        Optional.of(pattern),
        Optional.empty(),
        Optional.of(Color.web("#AABBCCDD")),
        null
    );
    knot.setRotationAngle(15);
    knot.setZoomFactor(2);
    knot.setVisible(true);
    knot.setSelectable(false);
    knot.setFlippedVertically(true);
    knot.setFlippedHorizontally(true);
    knot.setTypedText(new StringBuilder("x"));

    Step step = new Step();
    step.setStepIndex(1);
    step.setDisplayedKnots(List.of(knot));
    step.setSelectedKnots(List.of());

    diagram.getPatterns().clear();
    diagram.addPattern(pattern);
    diagram.setAllSteps(List.of(step));

    DiagramDescriptor proto = DiagramPersistenceMapper.toProto(diagram);
    Diagram restored = DiagramPersistenceMapper.fromProto(proto, diagram);
    DiagramSemanticComparator.assertSemanticallyEquivalent(diagram, restored);
    assertTrue(restored.getPatterns().stream().anyMatch(p -> "f.jpg".equals(p.getFilename())));
  }

  @Test
  void diagramPersistenceMapper_rejectsUnsupportedSchemaVersion() {
    DiagramDescriptor proto = DiagramDescriptor.newBuilder()
        .setSchemaVersion(99)
        .setName("x")
        .build();

    App app = new App();
    app.setMovablePane(new Pane());
    Diagram template = new Diagram(app);

    LaceArchiveException ex = assertThrows(LaceArchiveException.class,
        () -> DiagramPersistenceMapper.fromProto(proto, template));
    assertTrue(ex.getMessage().contains("Unsupported descriptor schema version"),
        ex.getMessage());
  }

  @Test
  void diagramPersistenceMapper_protoGridUnspecified_mapsToCrissCross() {
    DiagramDescriptor proto = DiagramDescriptor.newBuilder()
        .setSchemaVersion(1)
        .setGridType(org.alienlabs.adaloveslace.persistence.protobuf.GridType.GRID_TYPE_UNSPECIFIED)
        .setCurrentStepIndex(0)
        .build();

    App app = new App();
    app.setMovablePane(new Pane());
    Diagram template = new Diagram(app);

    Diagram restored = DiagramPersistenceMapper.fromProto(proto, template);
    assertEquals(GridType.CRISS_CROSS, restored.getCurrentGridType());
  }

  @Test
  void diagramDescriptor_mergeFrom_roundTripMatchesEquals() throws Exception {
    DiagramDescriptor first = DiagramDescriptor.newBuilder()
        .setSchemaVersion(1)
        .setName("merged")
        .setCurrentStepIndex(2)
        .build();
    byte[] bytes = first.toByteArray();

    DiagramDescriptor merged = DiagramDescriptor.newBuilder()
        .mergeFrom(bytes)
        .build();
    assertEquals(first, merged);
  }
}
