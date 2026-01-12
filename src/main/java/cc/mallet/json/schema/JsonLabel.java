package cc.mallet.json.schema;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * JSON representation of a Mallet Label.
 * Stores the index into the target alphabet.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonLabel extends JsonTargetObject {

    @JsonProperty("index")
    private int index;

    @JsonProperty("entry")
    private String entry;

    public JsonLabel() {}

    public JsonLabel(int index, String entry) {
        this.index = index;
        this.entry = entry;
    }

    @Override
    public String getType() {
        return "Label";
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getEntry() {
        return entry;
    }

    public void setEntry(String entry) {
        this.entry = entry;
    }
}
