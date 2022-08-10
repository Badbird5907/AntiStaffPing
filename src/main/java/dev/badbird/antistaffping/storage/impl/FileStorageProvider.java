package dev.badbird.antistaffping.storage.impl;

import dev.badbird.antistaffping.AntiStaffPing;
import dev.badbird.antistaffping.objects.ServerConfig;
import dev.badbird.antistaffping.storage.StorageProvider;
import lombok.SneakyThrows;

import java.io.File;
import java.nio.file.Files;

public class FileStorageProvider implements StorageProvider {
    @Override
    public void init() {
        File dataFolder = new File("data");
        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }
    }

    @Override
    public void disable() {

    }

    @SneakyThrows
    @Override
    public void save(ServerConfig config) {
        File dataFolder = new File("data");
        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }
        File serverConfigFile = new File(dataFolder, config.getServerId() + ".json");
        if (!serverConfigFile.exists()) {
            serverConfigFile.createNewFile();
        }
        String json = AntiStaffPing.getGson().toJson(config);
        Files.write(serverConfigFile.toPath(), json.getBytes());
    }

    @SneakyThrows
    @Override
    public ServerConfig get(long serverId) {
        File dataFolder = new File("data");
        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }
        File serverConfigFile = new File(dataFolder, serverId + ".json");
        if (!serverConfigFile.exists()) {
            return new ServerConfig(serverId);
        }
        String json = new String(Files.readAllBytes(serverConfigFile.toPath()));
        return AntiStaffPing.getGson().fromJson(json, ServerConfig.class);
    }
}
