package cc.mallet.json.cli;

import cc.mallet.json.convert.InstanceListToJson;
import cc.mallet.json.convert.JsonToInstanceList;
import cc.mallet.json.schema.JsonInstanceList;
import cc.mallet.types.InstanceList;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.*;
import java.util.concurrent.Callable;

/**
 * Command-line interface for the Mallet JSON converter.
 */
@Command(name = "mallet-json",
         description = "Convert Mallet InstanceList files using JSON",
         mixinStandardHelpOptions = true,
         version = "1.0.0",
         subcommands = {
             ConverterCLI.ToJsonCommand.class,
             ConverterCLI.FromJsonCommand.class,
             ConverterCLI.ConvertCommand.class
         })
public class ConverterCLI {

    public static void main(String[] args) {
        int exitCode = new CommandLine(new ConverterCLI()).execute(args);
        System.exit(exitCode);
    }

    /**
     * Convert Mallet binary file to JSON.
     */
    @Command(name = "to-json",
             description = "Convert Mallet binary file to JSON",
             mixinStandardHelpOptions = true)
    static class ToJsonCommand implements Callable<Integer> {

        @Option(names = {"-i", "--input"},
                required = true,
                description = "Input Mallet binary file (.mallet)")
        private File input;

        @Option(names = {"-o", "--output"},
                required = true,
                description = "Output JSON file (.json)")
        private File output;

        @Option(names = {"--pretty"},
                description = "Pretty-print JSON output")
        private boolean pretty;

        @Override
        public Integer call() {
            try {
                System.out.println("Reading Mallet file: " + input.getAbsolutePath());

                // Load InstanceList
                InstanceList instanceList = InstanceList.load(input);
                System.out.println("Loaded " + instanceList.size() + " instances");

                // Convert to JSON
                InstanceListToJson converter = new InstanceListToJson();
                JsonInstanceList json = converter.convert(instanceList);

                // Write JSON
                ObjectMapper mapper = new ObjectMapper();
                if (pretty) {
                    mapper.enable(SerializationFeature.INDENT_OUTPUT);
                }
                mapper.writeValue(output, json);

                System.out.println("Wrote JSON to: " + output.getAbsolutePath());
                System.out.println("Data alphabet size: " +
                    (json.getAlphabets() != null && json.getAlphabets().getData() != null
                        ? json.getAlphabets().getData().size() : 0));
                System.out.println("Target alphabet size: " +
                    (json.getAlphabets() != null && json.getAlphabets().getTarget() != null
                        ? json.getAlphabets().getTarget().size() : 0));

                return 0;

            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                e.printStackTrace();
                return 1;
            }
        }
    }

    /**
     * Convert JSON file to Mallet binary.
     */
    @Command(name = "from-json",
             description = "Convert JSON file to Mallet binary",
             mixinStandardHelpOptions = true)
    static class FromJsonCommand implements Callable<Integer> {

        @Option(names = {"-i", "--input"},
                required = true,
                description = "Input JSON file (.json)")
        private File input;

        @Option(names = {"-o", "--output"},
                required = true,
                description = "Output Mallet binary file (.mallet)")
        private File output;

        @Override
        public Integer call() {
            try {
                System.out.println("Reading JSON file: " + input.getAbsolutePath());

                // Read JSON
                ObjectMapper mapper = new ObjectMapper();
                JsonInstanceList json = mapper.readValue(input, JsonInstanceList.class);
                System.out.println("Loaded " + json.getInstances().size() + " instances from JSON");

                // Convert to InstanceList
                JsonToInstanceList converter = new JsonToInstanceList();
                InstanceList instanceList = converter.convert(json);

                // Save InstanceList
                instanceList.save(output);

                System.out.println("Wrote Mallet file to: " + output.getAbsolutePath());
                System.out.println("Data alphabet size: " +
                    (instanceList.getDataAlphabet() != null ? instanceList.getDataAlphabet().size() : 0));
                System.out.println("Target alphabet size: " +
                    (instanceList.getTargetAlphabet() != null ? instanceList.getTargetAlphabet().size() : 0));

                return 0;

            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                e.printStackTrace();
                return 1;
            }
        }
    }

    /**
     * Direct conversion from old Mallet format to new format via JSON.
     */
    @Command(name = "convert",
             description = "Convert Mallet file from old format to new format",
             mixinStandardHelpOptions = true)
    static class ConvertCommand implements Callable<Integer> {

        @Option(names = {"-i", "--input"},
                required = true,
                description = "Input Mallet binary file (old format)")
        private File input;

        @Option(names = {"-o", "--output"},
                required = true,
                description = "Output Mallet binary file (new format)")
        private File output;

        @Option(names = {"--keep-json"},
                description = "Keep intermediate JSON file")
        private boolean keepJson;

        @Option(names = {"--json-file"},
                description = "Path for intermediate JSON file (implies --keep-json)")
        private File jsonFile;

        @Override
        public Integer call() {
            try {
                System.out.println("Converting: " + input.getAbsolutePath());

                // Load InstanceList
                InstanceList instanceList = InstanceList.load(input);
                System.out.println("Loaded " + instanceList.size() + " instances");

                // Convert to JSON
                InstanceListToJson toJson = new InstanceListToJson();
                JsonInstanceList json = toJson.convert(instanceList);

                // Optionally save JSON
                if (keepJson || jsonFile != null) {
                    File jsonOutput = jsonFile != null ? jsonFile :
                        new File(output.getParentFile(), output.getName().replace(".mallet", ".json"));
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.enable(SerializationFeature.INDENT_OUTPUT);
                    mapper.writeValue(jsonOutput, json);
                    System.out.println("Saved JSON to: " + jsonOutput.getAbsolutePath());
                }

                // Convert back to InstanceList
                JsonToInstanceList fromJson = new JsonToInstanceList();
                InstanceList newInstanceList = fromJson.convert(json);

                // Save new InstanceList
                newInstanceList.save(output);

                System.out.println("Wrote new format to: " + output.getAbsolutePath());
                System.out.println("Conversion complete.");

                return 0;

            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                e.printStackTrace();
                return 1;
            }
        }
    }
}
