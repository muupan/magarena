package magic.data;

import magic.model.MagicCardDefinition;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TokenCardDefinitions {

    private static final Map<String,MagicCardDefinition> tokensMap = new HashMap<String, MagicCardDefinition>();

    public static Collection<MagicCardDefinition> getAll() {
        return tokensMap.values();
    }

    public static MagicCardDefinition get(final String name) {
        final String key = name.toLowerCase();
        if (tokensMap.containsKey(key)) {
            return tokensMap.get(key);
        } else {
            throw new RuntimeException("token not found: " + name);
        }
    }

    public static void add(final MagicCardDefinition token) {
        tokensMap.put(token.getFullName().toLowerCase(), token);
    }
}
