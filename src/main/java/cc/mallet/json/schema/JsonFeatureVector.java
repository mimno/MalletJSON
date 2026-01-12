package cc.mallet.json.schema;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * JSON representation of a Mallet FeatureVector.
 * Stores sparse feature indices and values.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonFeatureVector extends JsonDataObject {

    @JsonProperty("indices")
    private int[] indices;

    @JsonProperty("values")
    private double[] values;

    @JsonProperty("binary")
    private boolean binary;

    @JsonProperty("sparse")
    private boolean sparse = true;

    public JsonFeatureVector() {}

    @Override
    public String getType() {
        return "FeatureVector";
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

    public boolean isBinary() {
        return binary;
    }

    public void setBinary(boolean binary) {
        this.binary = binary;
    }

    public boolean isSparse() {
        return sparse;
    }

    public void setSparse(boolean sparse) {
        this.sparse = sparse;
    }

    public int numLocations() {
        return indices != null ? indices.length : 0;
    }
}
