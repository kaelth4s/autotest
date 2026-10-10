package ru.kaelth4s.service;

import ru.kaelth4s.dto.ConfigData;
import ru.kaelth4s.util.JsonUtils;

public class ConfigDataManager {
    private static final String configFileName = "config.json";
    private static ConfigData configData;

    private ConfigDataManager() {}

    public static ConfigData getConfigData() {
        if (configData != null)
            return configData;

        configData = JsonUtils.mapFromResources(configFileName, ConfigData.class);
        return configData;
    }
}
