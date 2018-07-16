package ru.skyfire.minecraft.guibuilder.util;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.spongepowered.api.plugin.PluginContainer;

import java.io.IOException;
import java.nio.file.Path;

public class ConfigUtil {
    public static ConfigurationNode initConfigBase(Path defaultConfig, PluginContainer plugin, String configName,
                                                   ConfigurationLoader configLoader) {
        if (!defaultConfig.toFile().exists()) {
            try {
                plugin.getAsset(configName).orElse(null).copyToFile(defaultConfig);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            return configLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ConfigurationNode initConfig(ConfigurationLoader configLoader) {
        if(configLoader==null){
            return null;
        }
        try {
            return configLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ConfigurationLoader getLoader(Path configPath, PluginContainer plugin, String name){
        Path translationPath = configPath.resolve(name);
        if (!translationPath.toFile().exists()) {
            try {
                plugin.getAsset(name).orElse(null).copyToDirectory(configPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return HoconConfigurationLoader.builder().setPath(translationPath).build();
    }
}
