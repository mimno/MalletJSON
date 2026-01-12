package cc.mallet.json;

import cc.mallet.json.cli.ConverterCLI;

/**
 * Main entry point for the Mallet JSON Converter.
 *
 * This tool converts Mallet InstanceList files between versions using JSON
 * as an intermediate format. It supports migration from Mallet 2.0.8
 * (GNU Trove) to Mallet 2.1+ (HPPC).
 *
 * Usage:
 *   java -jar mallet-json.jar to-json -i input.mallet -o output.json
 *   java -jar mallet-json.jar from-json -i input.json -o output.mallet
 *   java -jar mallet-json.jar convert -i old.mallet -o new.mallet
 *
 * @see cc.mallet.json.cli.ConverterCLI
 */
public class MalletJsonConverter {

    public static void main(String[] args) {
        ConverterCLI.main(args);
    }
}
