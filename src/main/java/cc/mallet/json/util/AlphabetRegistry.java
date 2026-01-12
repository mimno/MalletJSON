package cc.mallet.json.util;

import cc.mallet.types.Alphabet;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Registry that tracks Alphabet objects by their ID to maintain
 * identity semantics across serialization/deserialization.
 *
 * This mirrors the readResolve pattern in Mallet's Alphabet.java.
 * Handles both Mallet 2.0.8 (VMID) and 2.1+ (UUID) instance IDs.
 */
public class AlphabetRegistry {

    private final Map<String, Alphabet> alphabetsById = new ConcurrentHashMap<>();
    private final Map<Alphabet, String> idsByAlphabet = new ConcurrentHashMap<>();

    public AlphabetRegistry() {}

    /**
     * Register an alphabet with its ID.
     */
    public void register(String id, Alphabet alphabet) {
        alphabetsById.put(id, alphabet);
        idsByAlphabet.put(alphabet, id);
    }

    /**
     * Get an alphabet by its ID.
     */
    public Alphabet get(String id) {
        return alphabetsById.get(id);
    }

    /**
     * Get the ID for an alphabet.
     */
    public String getUuid(Alphabet alphabet) {
        return idsByAlphabet.get(alphabet);
    }

    /**
     * Get or create an alphabet, ensuring identity is preserved.
     */
    public Alphabet getOrCreate(String id, Supplier<Alphabet> factory) {
        return alphabetsById.computeIfAbsent(id, k -> {
            Alphabet alphabet = factory.get();
            idsByAlphabet.put(alphabet, id);
            return alphabet;
        });
    }

    /**
     * Register an alphabet, generating an ID if it doesn't have one.
     * Uses reflection to handle both Mallet 2.0.8 (VMID) and 2.1+ (UUID).
     */
    public String registerWithGeneratedUuid(Alphabet alphabet) {
        String existingId = idsByAlphabet.get(alphabet);
        if (existingId != null) {
            return existingId;
        }

        // Try to get the instance ID using reflection (works with both VMID and UUID)
        String id = getInstanceIdAsString(alphabet);
        if (id == null) {
            id = UUID.randomUUID().toString();
        }

        register(id, alphabet);
        return id;
    }

    /**
     * Get the instance ID from an alphabet as a string.
     * Uses reflection to handle both Mallet versions.
     */
    private String getInstanceIdAsString(Alphabet alphabet) {
        try {
            Method getInstanceId = Alphabet.class.getMethod("getInstanceId");
            Object instanceId = getInstanceId.invoke(alphabet);
            if (instanceId != null) {
                return instanceId.toString();
            }
        } catch (Exception e) {
            // Method doesn't exist or failed - generate a new ID
        }
        return null;
    }

    /**
     * Clear the registry.
     */
    public void clear() {
        alphabetsById.clear();
        idsByAlphabet.clear();
    }

    /**
     * Check if an alphabet is registered.
     */
    public boolean contains(Alphabet alphabet) {
        return idsByAlphabet.containsKey(alphabet);
    }

    /**
     * Check if an ID is registered.
     */
    public boolean containsUuid(String id) {
        return alphabetsById.containsKey(id);
    }
}
