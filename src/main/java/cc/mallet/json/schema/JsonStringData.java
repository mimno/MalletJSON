package cc.mallet.json.schema;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * JSON representation of raw string data.
 * Used when Instance.data is a plain String.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonStringData extends JsonDataObject {

    @JsonProperty("content")
    private String content;

    public JsonStringData() {}

    public JsonStringData(String content) {
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
