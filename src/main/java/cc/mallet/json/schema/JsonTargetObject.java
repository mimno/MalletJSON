package cc.mallet.json.schema;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Base class for Instance target objects (labels).
 * Supports polymorphic deserialization via Jackson type info.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = JsonLabel.class, name = "Label"),
    @JsonSubTypes.Type(value = JsonLabelVector.class, name = "LabelVector"),
    @JsonSubTypes.Type(value = JsonStringTarget.class, name = "String"),
    @JsonSubTypes.Type(value = JsonRawTarget.class, name = "Raw")
})
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class JsonTargetObject {

    @JsonProperty("alphabetRef")
    protected String alphabetRef;

    public String getAlphabetRef() {
        return alphabetRef;
    }

    public void setAlphabetRef(String alphabetRef) {
        this.alphabetRef = alphabetRef;
    }

    public abstract String getType();
}
