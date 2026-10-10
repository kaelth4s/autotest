package ru.kaelth4s.framework.manager;

import ru.kaelth4s.framework.dto.ConfigData;
import ru.kaelth4s.framework.util.JsonUtil;

public class ConfigManager {
    private static final String configFileName = "config.json";
    private static ConfigData configData;

    private ConfigManager() {}

    public static ConfigData getConfigData() {
        if (configData != null) return configData;
        configData = JsonUtil.mapFromResourcesJson(configFileName, ConfigData.class);
        return configData;
    }
}