package cc.mallet.json.schema;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

/**
 * JSON representation of a Mallet Alphabet.
 * Captures the bidirectional mapping between objects and integer indices.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonAlphabet {

    @JsonProperty("id")
    private String id;

    @JsonProperty("entryClass")
    private String entryClass;

    @JsonProperty("growthStopped")
    private boolean growthStopped;

    @JsonProperty("entries")
    private List<String> entries = new ArrayList<>();

    @JsonProperty("isLabelAlphabet")
    private boolean labelAlphabet;

    public JsonAlphabet() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEntryClass() {
        return entryClass;
    }

    public void setEntryClass(String entryClass) {
        this.entryClass = entryClass;
    }

    public boolean isGrowthStopped() {
        return growthStopped;
    }

    public void setGrowthStopped(boolean growthStopped) {
        this.growthStopped = growthStopped;
    }

    public List<String> getEntries() {
        return entries;
    }

    public void setEntries(List<String> entries) {
        this.entries = entries;
    }

    public boolean isLabelAlphabet() {
        return labelAlphabet;
    }

    public void setLabelAlphabet(boolean labelAlphabet) {
        this.labelAlphabet = labelAlphabet;
    }

    public void addEntry(String entry) {
        this.entries.add(entry);
    }

    public int size() {
        return entries.size();
    }
}
