package dev.digitality.velocitycommands.managers;

import com.velocitypowered.api.event.Subscribe;
import dev.digitality.velocitycommands.DigitalMain;
import dev.digitality.velocitycommands.utils.ChatUtils;
import lombok.Getter;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;

public class ConfigManager {
    @Getter
    private static Configuration config;

    public static void reloadConfigs() {
        CommandOverrideManager.getDisabledCommands().clear();
        CommandOverrideManager.getOverridenCommands().clear();

        config = loadConfig("settings.yml");
        if (config == null) return;

        if (!config.contains("disable-tab-complete"))
            config.set("disable-tab-complete", List.of("ver", "version", "icanhasbukkit"));

        config.getSection("overriden-commands").getKeys().forEach(key -> 
            CommandOverrideManager.getOverridenCommands().put(key.toLowerCase(), 
            ChatUtils.colorize(config.getString("overriden-commands." + key))));
        
        CommandOverrideManager.getDisabledCommands().addAll(config.getStringList("disabled-commands").stream().map(String::toLowerCase).toList());
        CommandOverrideManager.getDisabledAutocompleteCommands().addAll(config.getStringList("disable-tab-complete").stream().map(String::toLowerCase).toList());
    }

    public static Configuration loadConfig(String fileName) {
        File dataFolder = DigitalMain.getInstance().getDataDirectory().toFile();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        File file = new File(dataFolder, fileName);
        if (!file.exists()) {
            try (InputStream in = ConfigManager.class.getResourceAsStream("/" + fileName)) {
                if (in != null) Files.copy(in, file.toPath());
                else Files.createFile(file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            return ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void saveConfig(Configuration configuration, String fileName) {
        try {
            File file = new File(DigitalMain.getInstance().getDataDirectory().toFile(), fileName);
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
