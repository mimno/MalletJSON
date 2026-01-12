package cc.mallet.json.schema;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Base class for Instance data objects.
 * Supports polymorphic deserialization via Jackson type info.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = JsonFeatureVector.class, name = "FeatureVector"),
    @JsonSubTypes.Type(value = JsonFeatureSequence.class, name = "FeatureSequence"),
    @JsonSubTypes.Type(value = JsonStringData.class, name = "String"),
    @JsonSubTypes.Type(value = JsonRawData.class, name = "Raw")
})
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class JsonDataObject {

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
