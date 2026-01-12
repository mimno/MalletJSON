package cc.mallet.json.schema;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * JSON representation of a Mallet FeatureSequence.
 * Stores a sequence of feature indices (e.g., word tokens in a document).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonFeatureSequence extends JsonDataObject {

    @JsonProperty("features")
    private int[] features;

    @JsonProperty("length")
    private int length;

    public JsonFeatureSequence() {}

    @Override
    public String getType() {
        return "FeatureSequence";
    }

    public int[] getFeatures() {
        return features;
    }

    public void setFeatures(int[] features) {
        this.features = features;
        this.length = features != null ? features.length : 0;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
}
