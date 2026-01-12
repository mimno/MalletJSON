package cc.mallet.json.schema;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * JSON representation of a Mallet Pipe.
 * Pipes transform raw data into features; this stores metadata for reconstruction.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonPipe {

    @JsonProperty("className")
    private String className;

    @JsonProperty("id")
    private String id;

    @JsonProperty("dataAlphabetRef")
    private String dataAlphabetRef;

    @JsonProperty("targetAlphabetRef")
    private String targetAlphabetRef;

    @JsonProperty("targetProcessing")
    private boolean targetProcessing = true;

    @JsonProperty("children")
    private List<JsonPipe> children;

    public JsonPipe() {}

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDataAlphabetRef() {
        return dataAlphabetRef;
    }

    public void setDataAlphabetRef(String dataAlphabetRef) {
        this.dataAlphabetRef = dataAlphabetRef;
    }

    public String getTargetAlphabetRef() {
        return targetAlphabetRef;
    }

    public void setTargetAlphabetRef(String targetAlphabetRef) {
        this.targetAlphabetRef = targetAlphabetRef;
    }

    public boolean isTargetProcessing() {
        return targetProcessing;
    }

    public void setTargetProcessing(boolean targetProcessing) {
        this.targetProcessing = targetProcessing;
    }

    public List<JsonPipe> getChildren() {
        return children;
    }

    public void setChildren(List<JsonPipe> children) {
        this.children = children;
    }
}
