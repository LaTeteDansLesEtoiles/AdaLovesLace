### `.lace` archive format

- **Container**: a `.lace` file is a standard ZIP archive with a different extension.
- **Entries**:
  - `meta.properties`: archive metadata (at least `archive.version=1` for protobuf-based files).
  - `descriptor.pb`: primary binary descriptor encoded with Protocol Buffers.
  - `descriptor.json` (optional, debug only): JSON representation of the same protobuf descriptor.
  - Pattern image files: one entry per image, using the pattern filename (for example `snowflake.jpg`).
  - Legacy `save.xml` (optional, read-only): JAXB/XML descriptor used only for backward compatibility.

### Resource validation
At load time, the runtime verifies that every pattern filename referenced in the protobuf descriptor (`descriptor.pb`) is present as a ZIP entry in the `.lace` archive. If a referenced pattern file is missing, loading fails with an explicit persistence exception.

### Loading precedence

- If `descriptor.pb` exists, it is always used.
- Otherwise, if `save.xml` exists, the legacy JAXB/XML compatibility loader is used.
- If neither entry exists, loading fails with an explicit exception.
- If both `descriptor.pb` and `save.xml` are present, `descriptor.pb` wins and the XML entry is ignored (a debug/info message is logged).

