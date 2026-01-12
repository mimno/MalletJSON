package cc.mallet.json.schema;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JSON representation of a Mallet InstanceList.
 * This is the top-level container for serialized instance data.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonInstanceList {

    @JsonProperty("version")
    private String version = "1.0";

    @JsonProperty("alphabets")
    private JsonAlphabets alphabets;

    @JsonProperty("pipe")
    private JsonPipe pipe;

    @JsonProperty("instances")
    private List<JsonInstance> instances = new ArrayList<>();

    @JsonProperty("instanceWeights")
    private Map<Integer, Double> instanceWeights = new HashMap<>();

    public JsonInstanceList() {}

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public JsonAlphabets getAlphabets() {
        return alphabets;
    }

    public void setAlphabets(JsonAlphabets alphabets) {
        this.alphabets = alphabets;
    }

    public JsonPipe getPipe() {
        return pipe;
    }

    public void setPipe(JsonPipe pipe) {
        this.pipe = pipe;
    }

    public List<JsonInstance> getInstances() {
        return instances;
    }

    public void setInstances(List<JsonInstance> instances) {
        this.instances = instances;
    }

    public Map<Integer, Double> getInstanceWeights() {
        return instanceWeights;
    }

    public void setInstanceWeights(Map<Integer, Double> instanceWeights) {
        this.instanceWeights = instanceWeights;
    }

    public void addInstance(JsonInstance instance) {
        this.instances.add(instance);
    }

    public void setInstanceWeight(int index, double weight) {
        if (weight != 1.0) {
            this.instanceWeights.put(index, weight);
        }
    }

    /**
     * Container for data and target alphabets.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class JsonAlphabets {
        @JsonProperty("data")
        private JsonAlphabet data;

        @JsonProperty("target")
        private JsonAlphabet target;

        public JsonAlphabets() {}

        public JsonAlphabet getData() {
            return data;
        }

        public void setData(JsonAlphabet data) {
            this.data = data;
        }

        public JsonAlphabet getTarget() {
            return target;
        }

        public void setTarget(JsonAlphabet target) {
            this.target = target;
        }
    }
}
