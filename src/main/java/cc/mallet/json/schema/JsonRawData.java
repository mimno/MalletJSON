package cc.mallet.json.schema;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * JSON representation for unsupported or unknown data types.
 * Stores the class name and a string representation.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonRawData extends JsonDataObject {

    @JsonProperty("className")
    private String className;

    @JsonProperty("stringValue")
    private String stringValue;

    public JsonRawData() {}

    public JsonRawData(String className, String stringValue) {
        this.className = className;
        this.stringValue = stringValue;
    }

    @Override
    public String getType() {
        return "Raw";
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }
}
