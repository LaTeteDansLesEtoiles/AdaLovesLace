### Descriptor format migration overview

- **Old behavior**
  - `.lace` archives contained a JAXB/XML descriptor stored as `save.xml`.
  - Loading always unmarshalled `save.xml` directly into the domain `Diagram`.
  - Saving rebuilt `save.xml` into a temporary file and added it to the ZIP archive.

- **New behavior**
  - The primary descriptor is now a Protocol Buffers message (`descriptor.pb`) stored in the `.lace` ZIP archive.
  - New archives also contain `meta.properties` with `archive.version=1`.
  - Legacy `save.xml` is still supported, but only for backward compatibility when no `descriptor.pb` is present.

- **Loading rules**
  - If `descriptor.pb` exists, it is loaded via protobuf and mapped to the domain `Diagram`.
  - Else, if `save.xml` exists, it is loaded via the isolated JAXB compatibility loader.
  - If neither entry exists, loading fails with a clear exception.
  - If both entries exist, `descriptor.pb` takes precedence and the XML is ignored (a debug/info log records this case).

- **Saving rules**
  - New save operations write:
    - `meta.properties` with `archive.version=1`.
    - `descriptor.pb` with the protobuf-encoded descriptor.
    - All pattern image files, as before.
  - `save.xml` is no longer written by the default save path.

- **Compatibility**
  - Existing `.lace` archives containing only `save.xml` continue to load via the legacy JAXB path.
  - New `.lace` archives created by this version will not contain `save.xml` by default.

- **Benchmarking (developer-only)**
  - The class `org.alienlabs.adaloveslace.persistence.LaceArchiveBenchmark` can measure load time for both legacy XML (`save.xml`) and protobuf (`descriptor.pb`) inside the same `.lace` archive.
  - Run it from your IDE (right-click `LaceArchiveBenchmark` → `Run`) or use it as a small CLI:
    - After building the project (`mvn -q -DskipTests package`), pass one or more `.lace` file paths as arguments to the `main(String[] args)` method.
  - Notes:
    - It’s intended for local development only (best-effort logging, not a statistically rigorous benchmark harness).
    - If an archive does not contain `save.xml` or `descriptor.pb`, only the available path will be measured.

