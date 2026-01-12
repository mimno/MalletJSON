package cc.mallet.json.util;

import cc.mallet.types.*;

/**
 * Utility class for detecting polymorphic data types in Instance objects.
 *
 * Instance.data can be: FeatureVector, FeatureSequence, AugmentableFeatureVector, String, etc.
 * Instance.target can be: Label, LabelVector, Labels, String, etc.
 */
public class DataTypeDetector {

    public enum DataType {
        FEATURE_VECTOR,
        AUGMENTABLE_FEATURE_VECTOR,
        FEATURE_SEQUENCE,
        STRING,
        OTHER
    }

    public enum TargetType {
        LABEL,
        LABEL_VECTOR,
        LABELS,
        STRING,
        OTHER
    }

    /**
     * Detect the type of Instance.data object.
     */
    public static DataType detectDataType(Object data) {
        if (data == null) {
            return DataType.OTHER;
        }
        if (data instanceof AugmentableFeatureVector) {
            return DataType.AUGMENTABLE_FEATURE_VECTOR;
        }
        if (data instanceof FeatureVector) {
            return DataType.FEATURE_VECTOR;
        }
        if (data instanceof FeatureSequence) {
            return DataType.FEATURE_SEQUENCE;
        }
        if (data instanceof String) {
            return DataType.STRING;
        }
        return DataType.OTHER;
    }

    /**
     * Detect the type of Instance.target object.
     */
    public static TargetType detectTargetType(Object target) {
        if (target == null) {
            return TargetType.OTHER;
        }
        if (target instanceof LabelVector) {
            return TargetType.LABEL_VECTOR;
        }
        if (target instanceof Label) {
            return TargetType.LABEL;
        }
        if (target instanceof Labels) {
            return TargetType.LABELS;
        }
        if (target instanceof String) {
            return TargetType.STRING;
        }
        return TargetType.OTHER;
    }

    /**
     * Get the data alphabet from a data object if available.
     */
    public static Alphabet getDataAlphabet(Object data) {
        if (data instanceof AlphabetCarrying) {
            Alphabet[] alphabets = ((AlphabetCarrying) data).getAlphabets();
            if (alphabets != null && alphabets.length > 0) {
                return alphabets[0];
            }
        }
        return null;
    }

    /**
     * Get the target alphabet from a target object if available.
     */
    public static Alphabet getTargetAlphabet(Object target) {
        if (target instanceof AlphabetCarrying) {
            Alphabet[] alphabets = ((AlphabetCarrying) target).getAlphabets();
            if (alphabets != null && alphabets.length > 0) {
                return alphabets[0];
            }
        }
        return null;
    }
}
