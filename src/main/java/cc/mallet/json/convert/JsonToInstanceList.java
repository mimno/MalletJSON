package cc.mallet.json.convert;

import cc.mallet.json.schema.*;
import cc.mallet.json.util.AlphabetRegistry;
import cc.mallet.pipe.Noop;
import cc.mallet.pipe.Pipe;
import cc.mallet.types.*;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Converts JSON representation back to Mallet InstanceList objects.
 */
public class JsonToInstanceList {

    private final AlphabetRegistry alphabetRegistry = new AlphabetRegistry();
    private Alphabet dataAlphabet;
    private LabelAlphabet targetAlphabet;

    /**
     * Convert JSON representation to a Mallet InstanceList.
     */
    public InstanceList convert(JsonInstanceList json) {
        // First, reconstruct alphabets
        if (json.getAlphabets() != null) {
            if (json.getAlphabets().getData() != null) {
                dataAlphabet = convertAlphabet(json.getAlphabets().getData());
            }
            if (json.getAlphabets().getTarget() != null) {
                JsonAlphabet targetJson = json.getAlphabets().getTarget();
                targetAlphabet = convertLabelAlphabet(targetJson);
            }
        }

        // Create pipe (simplified - just holds alphabets)
        Pipe pipe = createPipe(json.getPipe());

        // Create instance list
        InstanceList instanceList = new InstanceList(pipe);

        // Convert instances
        List<JsonInstance> instances = json.getInstances();
        if (instances != null) {
            for (JsonInstance jsonInstance : instances) {
                Instance instance = convertInstance(jsonInstance);
                instanceList.add(instance);
            }
        }

        // Set instance weights
        Map<Integer, Double> weights = json.getInstanceWeights();
        if (weights != null) {
            for (Map.Entry<Integer, Double> entry : weights.entrySet()) {
                instanceList.setInstanceWeight(entry.getKey(), entry.getValue());
            }
        }

        return instanceList;
    }

    /**
     * Convert JSON alphabet to Mallet Alphabet.
     */
    public Alphabet convertAlphabet(JsonAlphabet json) {
        Alphabet alphabet = new Alphabet();

        // Add entries
        List<String> entries = json.getEntries();
        if (entries != null) {
            for (String entry : entries) {
                alphabet.lookupIndex(entry, true);
            }
        }

        // Set growth stopped
        if (json.isGrowthStopped()) {
            alphabet.stopGrowth();
        }

        // Set instance ID using reflection (handles both UUID for 2.1+ and VMID for 2.0.8)
        setInstanceIdFromString(alphabet, json.getId());

        // Register in registry
        if (json.getId() != null) {
            alphabetRegistry.register(json.getId(), alphabet);
        }

        return alphabet;
    }

    /**
     * Convert JSON alphabet to Mallet LabelAlphabet.
     */
    public LabelAlphabet convertLabelAlphabet(JsonAlphabet json) {
        LabelAlphabet alphabet = new LabelAlphabet();

        // Add entries
        List<String> entries = json.getEntries();
        if (entries != null) {
            for (String entry : entries) {
                alphabet.lookupIndex(entry, true);
            }
        }

        // Set growth stopped
        if (json.isGrowthStopped()) {
            alphabet.stopGrowth();
        }

        // Set instance ID using reflection (handles both UUID for 2.1+ and VMID for 2.0.8)
        setInstanceIdFromString(alphabet, json.getId());

        // Register in registry
        if (json.getId() != null) {
            alphabetRegistry.register(json.getId(), alphabet);
        }

        return alphabet;
    }

    /**
     * Set the instance ID on an alphabet using reflection.
     * Handles both Mallet 2.0.8 (VMID) and 2.1+ (UUID).
     */
    private void setInstanceIdFromString(Alphabet alphabet, String idString) {
        if (idString == null) {
            return;
        }

        try {
            // Try Mallet 2.1+ approach (UUID)
            Method setInstanceId = Alphabet.class.getMethod("setInstanceId", UUID.class);
            UUID uuid = UUID.fromString(idString);
            setInstanceId.invoke(alphabet, uuid);
        } catch (Exception e) {
            // Either not a valid UUID, or Mallet 2.0.8 which uses VMID
            // For 2.0.8, we don't need to set the ID - it's auto-generated
        }
    }

    /**
     * Create a pipe from JSON representation.
     * This creates a simple Noop pipe that holds the alphabets.
     */
    public Pipe createPipe(JsonPipe json) {
        // Create a simple Noop pipe that holds the alphabets
        Noop pipe = new Noop(dataAlphabet, targetAlphabet);
        return pipe;
    }

    /**
     * Convert JSON instance to Mallet Instance.
     */
    public Instance convertInstance(JsonInstance json) {
        // Convert data
        Object data = null;
        if (json.getData() != null) {
            data = convertData(json.getData());
        }

        // Convert target
        Object target = null;
        if (json.getTarget() != null) {
            target = convertTarget(json.getTarget());
        }

        // Create instance
        Instance instance = new Instance(
            data,
            target,
            json.getName(),
            json.getSource()
        );

        // Add properties
        List<JsonInstance.InstanceProperty> properties = json.getProperties();
        if (properties != null) {
            for (JsonInstance.InstanceProperty prop : properties) {
                if (prop.isNumeric()) {
                    Object value = prop.getValue();
                    double numValue = (value instanceof Number) ? ((Number) value).doubleValue() : 0.0;
                    instance.setNumericProperty(prop.getKey(), numValue);
                } else {
                    Object value = prop.getValue();
                    instance.setProperty(prop.getKey(), value);
                }
            }
        }

        // Lock if needed
        if (json.isLocked()) {
            instance.lock();
        }

        return instance;
    }

    /**
     * Convert JSON data object to Mallet data object.
     */
    public Object convertData(JsonDataObject json) {
        if (json instanceof JsonFeatureVector) {
            return convertFeatureVector((JsonFeatureVector) json);
        } else if (json instanceof JsonFeatureSequence) {
            return convertFeatureSequence((JsonFeatureSequence) json);
        } else if (json instanceof JsonStringData) {
            return ((JsonStringData) json).getContent();
        } else if (json instanceof JsonRawData) {
            // For raw data, just return the string representation
            return ((JsonRawData) json).getStringValue();
        }
        return null;
    }

    /**
     * Convert JSON feature vector to Mallet FeatureVector.
     */
    public FeatureVector convertFeatureVector(JsonFeatureVector json) {
        // Get alphabet
        Alphabet alphabet = getAlphabet(json.getAlphabetRef(), dataAlphabet);

        int[] indices = json.getIndices();
        double[] values = json.getValues();

        if (json.isBinary() || values == null) {
            // Binary feature vector
            return new FeatureVector(alphabet, indices);
        } else {
            // Weighted feature vector
            return new FeatureVector(alphabet, indices, values);
        }
    }

    /**
     * Convert JSON feature sequence to Mallet FeatureSequence.
     */
    public FeatureSequence convertFeatureSequence(JsonFeatureSequence json) {
        // Get alphabet
        Alphabet alphabet = getAlphabet(json.getAlphabetRef(), dataAlphabet);

        int[] features = json.getFeatures();
        return new FeatureSequence(alphabet, features);
    }

    /**
     * Convert JSON target object to Mallet target object.
     */
    public Object convertTarget(JsonTargetObject json) {
        if (json instanceof JsonLabel) {
            return convertLabel((JsonLabel) json);
        } else if (json instanceof JsonLabelVector) {
            return convertLabelVector((JsonLabelVector) json);
        } else if (json instanceof JsonStringTarget) {
            return ((JsonStringTarget) json).getContent();
        } else if (json instanceof JsonRawTarget) {
            return ((JsonRawTarget) json).getStringValue();
        }
        return null;
    }

    /**
     * Convert JSON label to Mallet Label.
     */
    public Label convertLabel(JsonLabel json) {
        // Get label alphabet
        LabelAlphabet alphabet = getLabelAlphabet(json.getAlphabetRef());

        // Get or create label
        return alphabet.lookupLabel(json.getIndex());
    }

    /**
     * Convert JSON label vector to Mallet LabelVector.
     */
    public LabelVector convertLabelVector(JsonLabelVector json) {
        // Get label alphabet
        LabelAlphabet alphabet = getLabelAlphabet(json.getAlphabetRef());

        int[] indices = json.getIndices();
        double[] values = json.getValues();

        return new LabelVector(alphabet, indices, values);
    }

    /**
     * Get alphabet by reference, falling back to default.
     */
    private Alphabet getAlphabet(String ref, Alphabet defaultAlphabet) {
        if (ref != null) {
            Alphabet alphabet = alphabetRegistry.get(ref);
            if (alphabet != null) {
                return alphabet;
            }
        }
        return defaultAlphabet;
    }

    /**
     * Get label alphabet by reference.
     */
    private LabelAlphabet getLabelAlphabet(String ref) {
        if (ref != null) {
            Alphabet alphabet = alphabetRegistry.get(ref);
            if (alphabet instanceof LabelAlphabet) {
                return (LabelAlphabet) alphabet;
            }
        }
        return targetAlphabet;
    }

    /**
     * Get the alphabet registry used during conversion.
     */
    public AlphabetRegistry getAlphabetRegistry() {
        return alphabetRegistry;
    }

    /**
     * Get the data alphabet after conversion.
     */
    public Alphabet getDataAlphabet() {
        return dataAlphabet;
    }

    /**
     * Get the target alphabet after conversion.
     */
    public LabelAlphabet getTargetAlphabet() {
        return targetAlphabet;
    }
}
