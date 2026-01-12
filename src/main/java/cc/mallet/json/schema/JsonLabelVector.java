package cc.mallet.json.schema;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * JSON representation of a Mallet LabelVector.
 * Stores multiple labels with associated confidence values.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonLabelVector extends JsonTargetObject {

    @JsonProperty("indices")
    private int[] indices;

    @JsonProperty("values")
    private double[] values;

    @JsonProperty("bestIndex")
    private int bestIndex;

    public JsonLabelVector() {}

    @Override
    public String getType() {
        return "LabelVector";
    }

    public int[] getIndices() {
        return indices;
    }

    public void setIndices(int[] indices) {
        this.indices = indices;
    }

    public double[] getValues() {
        return values;
    }

    public void setValues(double[] values) {
        this.values = values;
    }

    public int getBestIndex() {
        return bestIndex;
    }

    public void setBestIndex(int bestIndex) {
        this.bestIndex = bestIndex;
    }
}
