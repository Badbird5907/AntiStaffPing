package dev.badbird.antistaffping.storage;

import dev.badbird.antistaffping.objects.ServerConfig;

public interface StorageProvider {

    void init();

    void disable();

    void save(ServerConfig config);

    ServerConfig get(long serverId);
}
