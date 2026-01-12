package cc.mallet.json.schema;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * JSON representation for string target values.
 * Used when Instance.target is a plain String.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonStringTarget extends JsonTargetObject {

    @JsonProperty("content")
    private String content;

    public JsonStringTarget() {}

    public JsonStringTarget(String content) {
        this.content = content;
    }

    @Override
    public String getType() {
        return "String";
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
