package cc.mallet.json.convert;

import cc.mallet.json.schema.*;
import cc.mallet.json.util.AlphabetRegistry;
import cc.mallet.json.util.DataTypeDetector;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.types.*;
import cc.mallet.util.PropertyList;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Converts Mallet InstanceList objects to JSON representation.
 */
public class InstanceListToJson {

    private final AlphabetRegistry alphabetRegistry = new AlphabetRegistry();

    /**
     * Convert a Mallet InstanceList to JSON representation.
     */
    public JsonInstanceList convert(InstanceList instanceList) {
        JsonInstanceList json = new JsonInstanceList();

        // Convert alphabets
        JsonInstanceList.JsonAlphabets alphabets = new JsonInstanceList.JsonAlphabets();

        Alphabet dataAlphabet = instanceList.getDataAlphabet();
        Alphabet targetAlphabet = instanceList.getTargetAlphabet();

        if (dataAlphabet != null) {
            alphabets.setData(convertAlphabet(dataAlphabet));
        }
        if (targetAlphabet != null) {
            alphabets.setTarget(convertAlphabet(targetAlphabet));
        }
        json.setAlphabets(alphabets);

        // Convert pipe
        Pipe pipe = instanceList.getPipe();
        if (pipe != null) {
            json.setPipe(convertPipe(pipe));
        }

        // Convert instances
        List<JsonInstance> instances = new ArrayList<>();
        for (int i = 0; i < instanceList.size(); i++) {
            Instance instance = instanceList.get(i);
            instances.add(convertInstance(instance));
        }
        json.setInstances(instances);

        // Convert instance weights
        Map<Integer, Double> weights = new HashMap<>();
        for (int i = 0; i < instanceList.size(); i++) {
            double weight = instanceList.getInstanceWeight(i);
            if (weight != 1.0) {
                weights.put(i, weight);
            }
        }
        if (!weights.isEmpty()) {
            json.setInstanceWeights(weights);
        }

        return json;
    }

    /**
     * Convert a Mallet Alphabet to JSON representation.
     */
    public JsonAlphabet convertAlphabet(Alphabet alphabet) {
        JsonAlphabet json = new JsonAlphabet();

        // Get or generate UUID
        String uuid = alphabetRegistry.registerWithGeneratedUuid(alphabet);
        json.setId(uuid);

        // Set entry class
        Class<?> entryClass = alphabet.entryClass();
        if (entryClass != null) {
            json.setEntryClass(entryClass.getName());
        }

        json.setGrowthStopped(alphabet.growthStopped());
        json.setLabelAlphabet(alphabet instanceof LabelAlphabet);

        // Convert entries
        List<String> entries = new ArrayList<>();
        for (int i = 0; i < alphabet.size(); i++) {
            Object entry = alphabet.lookupObject(i);
            entries.add(entry != null ? entry.toString() : "");
        }
        json.setEntries(entries);

        return json;
    }

    /**
     * Convert a Mallet Pipe to JSON representation.
     */
    public JsonPipe convertPipe(Pipe pipe) {
        JsonPipe json = new JsonPipe();

        json.setClassName(pipe.getClass().getName());

        // Get pipe's instance ID via reflection
        try {
            Field instanceIdField = Pipe.class.getDeclaredField("instanceId");
            instanceIdField.setAccessible(true);
            Object instanceId = instanceIdField.get(pipe);
            if (instanceId != null) {
                json.setId(instanceId.toString());
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            // instanceId field may not exist in all versions
        }

        // Get alphabet references
        Alphabet dataAlphabet = pipe.getDataAlphabet();
        Alphabet targetAlphabet = pipe.getTargetAlphabet();

        if (dataAlphabet != null && alphabetRegistry.contains(dataAlphabet)) {
            json.setDataAlphabetRef(alphabetRegistry.getUuid(dataAlphabet));
        }
        if (targetAlphabet != null && alphabetRegistry.contains(targetAlphabet)) {
            json.setTargetAlphabetRef(alphabetRegistry.getUuid(targetAlphabet));
        }

        json.setTargetProcessing(pipe.isTargetProcessing());

        // Handle SerialPipes (composite pipes)
        if (pipe instanceof SerialPipes) {
            SerialPipes serialPipes = (SerialPipes) pipe;
            List<JsonPipe> children = new ArrayList<>();
            for (Pipe child : serialPipes.pipes()) {
                children.add(convertPipe(child));
            }
            json.setChildren(children);
        }

        return json;
    }

    /**
     * Convert a Mallet Instance to JSON representation.
     */
    public JsonInstance convertInstance(Instance instance) {
        JsonInstance json = new JsonInstance();

        // Name
        Object name = instance.getName();
        if (name != null) {
            json.setName(name.toString());
        }

        // Source
        Object source = instance.getSource();
        if (source != null) {
            json.setSource(source.toString());
        }

        // Data
        Object data = instance.getData();
        if (data != null) {
            json.setData(convertData(data));
        }

        // Target
        Object target = instance.getTarget();
        if (target != null) {
            json.setTarget(convertTarget(target));
        }

        // Properties
        PropertyList properties = instance.getProperties();
        if (properties != null) {
            convertProperties(json, properties);
        }

        // Locked
        json.setLocked(instance.isLocked());

        return json;
    }

    /**
     * Convert Instance.data to JSON representation.
     */
    public JsonDataObject convertData(Object data) {
        DataTypeDetector.DataType type = DataTypeDetector.detectDataType(data);

        switch (type) {
            case FEATURE_VECTOR:
            case AUGMENTABLE_FEATURE_VECTOR:
                return convertFeatureVector((FeatureVector) data);

            case FEATURE_SEQUENCE:
                return convertFeatureSequence((FeatureSequence) data);

            case STRING:
                return new JsonStringData((String) data);

            default:
                return new JsonRawData(data.getClass().getName(), data.toString());
        }
    }

    /**
     * Convert a Mallet FeatureVector to JSON representation.
     */
    public JsonFeatureVector convertFeatureVector(FeatureVector fv) {
        JsonFeatureVector json = new JsonFeatureVector();

        // Alphabet reference
        Alphabet dictionary = fv.getAlphabet();
        if (dictionary != null) {
            String uuid = alphabetRegistry.registerWithGeneratedUuid(dictionary);
            json.setAlphabetRef(uuid);
        }

        // Get indices and values
        int numLocations = fv.numLocations();
        int[] indices = new int[numLocations];
        double[] values = null;

        boolean isBinary = true;
        for (int i = 0; i < numLocations; i++) {
            indices[i] = fv.indexAtLocation(i);
            double val = fv.valueAtLocation(i);
            if (val != 1.0) {
                isBinary = false;
            }
        }

        json.setIndices(indices);
        json.setBinary(isBinary);

        if (!isBinary) {
            values = new double[numLocations];
            for (int i = 0; i < numLocations; i++) {
                values[i] = fv.valueAtLocation(i);
            }
            json.setValues(values);
        }

        json.setSparse(true);

        return json;
    }

    /**
     * Convert a Mallet FeatureSequence to JSON representation.
     */
    public JsonFeatureSequence convertFeatureSequence(FeatureSequence fs) {
        JsonFeatureSequence json = new JsonFeatureSequence();

        // Alphabet reference
        Alphabet dictionary = fs.getAlphabet();
        if (dictionary != null) {
            String uuid = alphabetRegistry.registerWithGeneratedUuid(dictionary);
            json.setAlphabetRef(uuid);
        }

        // Get features
        int length = fs.getLength();
        int[] features = new int[length];
        for (int i = 0; i < length; i++) {
            features[i] = fs.getIndexAtPosition(i);
        }
        json.setFeatures(features);

        return json;
    }

    /**
     * Convert Instance.target to JSON representation.
     */
    public JsonTargetObject convertTarget(Object target) {
        DataTypeDetector.TargetType type = DataTypeDetector.detectTargetType(target);

        switch (type) {
            case LABEL:
                return convertLabel((Label) target);

            case LABEL_VECTOR:
                return convertLabelVector((LabelVector) target);

            case STRING:
                return new JsonStringTarget((String) target);

            default:
                return new JsonRawTarget(target.getClass().getName(), target.toString());
        }
    }

    /**
     * Convert a Mallet Label to JSON representation.
     */
    public JsonLabel convertLabel(Label label) {
        JsonLabel json = new JsonLabel();

        // Alphabet reference
        LabelAlphabet dictionary = label.getLabelAlphabet();
        if (dictionary != null) {
            String uuid = alphabetRegistry.registerWithGeneratedUuid(dictionary);
            json.setAlphabetRef(uuid);
        }

        json.setIndex(label.getIndex());
        json.setEntry(label.getEntry().toString());

        return json;
    }

    /**
     * Convert a Mallet LabelVector to JSON representation.
     */
    public JsonLabelVector convertLabelVector(LabelVector lv) {
        JsonLabelVector json = new JsonLabelVector();

        // Alphabet reference
        LabelAlphabet dictionary = lv.getLabelAlphabet();
        if (dictionary != null) {
            String uuid = alphabetRegistry.registerWithGeneratedUuid(dictionary);
            json.setAlphabetRef(uuid);
        }

        // Get indices and values
        int numLocations = lv.numLocations();
        int[] indices = new int[numLocations];
        double[] values = new double[numLocations];

        for (int i = 0; i < numLocations; i++) {
            indices[i] = lv.indexAtLocation(i);
            values[i] = lv.valueAtLocation(i);
        }

        json.setIndices(indices);
        json.setValues(values);
        json.setBestIndex(lv.getBestIndex());

        return json;
    }

    /**
     * Convert PropertyList to JSON properties using the Iterator API.
     */
    private void convertProperties(JsonInstance json, PropertyList properties) {
        PropertyList.Iterator iter = properties.iterator();
        while (iter.hasNext()) {
            iter.nextProperty();
            String key = iter.getKey();
            if (iter.isNumeric()) {
                double value = iter.getNumericValue();
                json.addProperty(key, value, true);
            } else {
                Object value = iter.getObjectValue();
                json.addProperty(key, value != null ? value.toString() : null, false);
            }
        }
    }

    /**
     * Get the alphabet registry used during conversion.
     */
    public AlphabetRegistry getAlphabetRegistry() {
        return alphabetRegistry;
    }
}
