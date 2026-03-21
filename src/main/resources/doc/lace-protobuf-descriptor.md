### Protobuf descriptor and versioning

- **Schema**: defined in `src/main/proto/diagram_descriptor.proto` as `DiagramDescriptor`.
- **Top-level fields**:
  - `schema_version`: descriptor schema version (currently `1`, required).
  - `name`, `grid_type`, `current_step_index`.
  - `steps`: list of `StepDescriptor` (displayed and selected knots).
  - `patterns`: list of `PatternDescriptor` (pattern filename and geometry).
- **Version handling**:
  - Only `schema_version = 1` is supported at the moment.
  - Future schema versions must bump this field and extend messages in a backward-compatible way.
  - Loading a descriptor with an unsupported `schema_version` fails explicitly.
- **Archive metadata**:
  - `meta.properties` contains `archive.version`.
  - Archives without `meta.properties` are treated as version `0` (legacy/compat mode).
  - Archives with `archive.version > 1` are rejected with an explicit error.

