### Debug / developer-readable descriptor export

- The runtime descriptor inside a `.lace` archive is always stored as binary protobuf in `descriptor.pb`.
- For debugging and development, a readable JSON representation can be produced in two ways:

- **1. Optional `descriptor.json` inside the archive**
  - The persistence layer (`LaceArchiveSaver`) supports a `SaveOptions` flag `writeDebugJson`.
  - When enabled, saving a `.lace` file will add a `descriptor.json` entry next to `descriptor.pb`.
  - This JSON entry is never used for normal loading and must remain a debug-only artifact.

- **2. On-demand JSON dump from an existing `.lace` file**
  - The helper `LaceArchiveLoader.dumpDescriptorJson(File laceFile)` returns a JSON string for the `descriptor.pb` entry.
  - This method does not modify the archive; it only reads and formats the binary descriptor.

- Both mechanisms are intended for troubleshooting and inspection only and are not required for normal application usage.

