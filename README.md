# MalletJSON

A tool for converting Mallet InstanceList files between versions using JSON as an intermediate format.

## Background

Mallet 2.0.8 used GNU Trove for primitive collections, while Mallet 2.1+ uses HPPC. This change breaks Java serialization compatibility. This tool provides a way to convert old Mallet data files to the new format.

## Building

Requires Java 11+ and Maven.

```bash
# Build reader (for Mallet 2.0.8 files)
mvn clean package -P mallet-208 -DskipTests

# Build writer (for Mallet 2.1 files)
mvn clean package -P mallet-21 -DskipTests
```

Note: The `mallet-21` profile expects Mallet 2.1.0 at `../Mallet/target/mallet-2.1.0.jar`. Adjust the path in `pom.xml` if needed.

## Usage

### Two-Step Conversion

**Step 1: Convert old format to JSON** (using reader built with Mallet 2.0.8)

```bash
mvn package -P mallet-208 -DskipTests
java -cp "target/mallet-json-1.0.0.jar:$(mvn -P mallet-208 dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout)" \
  cc.mallet.json.MalletJsonConverter to-json \
  -i old_data.mallet \
  -o data.json \
  --pretty
```

**Step 2: Convert JSON to new format** (using writer built with Mallet 2.1)

```bash
mvn package -P mallet-21 -DskipTests
java -cp "target/mallet-json-1.0.0.jar:$(mvn -P mallet-21 dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout)" \
  cc.mallet.json.MalletJsonConverter from-json \
  -i data.json \
  -o new_data.mallet
```

### Commands

- `to-json` - Convert Mallet binary to JSON
- `from-json` - Convert JSON to Mallet binary
- `convert` - Direct conversion (requires same Mallet version for read/write)

### Options

- `-i, --input` - Input file (required)
- `-o, --output` - Output file (required)
- `--pretty` - Pretty-print JSON output

## Supported Types

- `InstanceList` with `FeatureSequence` or `FeatureVector` data
- `Alphabet` and `LabelAlphabet`
- `Label` and `LabelVector` targets
- Instance properties and weights

## JSON Schema

The intermediate JSON format captures:

```json
{
  "version": "1.0",
  "alphabets": {
    "data": { "id": "...", "entries": ["word1", "word2", ...] },
    "target": { "id": "...", "entries": ["label1", "label2", ...] }
  },
  "instances": [
    {
      "name": "doc1",
      "data": { "type": "FeatureSequence", "features": [0, 5, 12, ...] },
      "target": { "type": "Label", "index": 0 }
    }
  ]
}
```

## License

MIT License - see LICENSE file.
