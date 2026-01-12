package cc.mallet.json.schema;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

/**
 * JSON representation of a Mallet Instance.
 * Contains the data (features), target (label), and metadata.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonInstance {

    @JsonProperty("name")
    private String name;

    @JsonProperty("source")
    private String source;

    @JsonProperty("data")
    private JsonDataObject data;

    @JsonProperty("target")
    private JsonTargetObject target;

    @JsonProperty("properties")
    private List<InstanceProperty> properties;

    @JsonProperty("locked")
    private boolean locked;

    public JsonInstance() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public JsonDataObject getData() {
        return data;
    }

    public void setData(JsonDataObject data) {
        this.data = data;
    }

    public JsonTargetObject getTarget() {
        return target;
    }

    public void setTarget(JsonTargetObject target) {
        this.target = target;
    }

    public List<InstanceProperty> getProperties() {
        return properties;
    }

    public void setProperties(List<InstanceProperty> properties) {
        this.properties = properties;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public void addProperty(String key, Object value, boolean isNumeric) {
        if (this.properties == null) {
            this.properties = new ArrayList<>();
        }
        InstanceProperty prop = new InstanceProperty();
        prop.setKey(key);
        prop.setValue(value);
        prop.setNumeric(isNumeric);
        this.properties.add(prop);
    }

    /**
     * Represents a key-value property from Mallet's PropertyList.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class InstanceProperty {
        @JsonProperty("key")
        private String key;

        @JsonProperty("value")
        private Object value;

        @JsonProperty("numeric")
        private boolean numeric;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public boolean isNumeric() {
            return numeric;
        }

        public void setNumeric(boolean numeric) {
            this.numeric = numeric;
        }
    }
}
