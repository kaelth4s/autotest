package ru.kaelth4s.framework.db.query;

import ru.kaelth4s.framework.db.base.HqlScript;

import java.util.HashMap;
import java.util.Map;

public record HqlQuery(
        HqlScript script,
        Map<String, String> substitutions,
        Map<String, Object> namedParams
) {
    private static final String PLACEHOLDER_FORMAT = "${%s}";

    public HqlQuery {
        substitutions = Map.copyOf(substitutions);
        namedParams = Map.copyOf(namedParams);
    }

    public static Builder builder(HqlScript script) {
        return new Builder(script);
    }

    public String substitute() {
        String text = script.hql();
        for (Map.Entry<String, String> entry : substitutions.entrySet()) {
            text = text.replace(PLACEHOLDER_FORMAT.formatted(entry.getKey()), entry.getValue());
        }
        return text;
    }

    public static final class Builder {
        private final HqlScript script;
        private final Map<String, String> substitutions = new HashMap<>();
        private final Map<String, Object> namedParams = new HashMap<>();

        private Builder(HqlScript script) {
            this.script = script;
        }

        public Builder substitutions(Map<String, String> values) {
            substitutions.putAll(values);
            return this;
        }

        public Builder namedParam(String key, Object value) {
            namedParams.put(key, value);
            return this;
        }

        public HqlQuery build() {
            return new HqlQuery(script, substitutions, namedParams);
        }
    }
}