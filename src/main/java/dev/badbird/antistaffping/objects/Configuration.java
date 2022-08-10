package dev.badbird.antistaffping.objects;

import dev.badbird.antistaffping.AntiStaffPing;
import dev.badbird.antistaffping.storage.StorageProvider;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.File;
import java.nio.file.Files;

public class Configuration {

    private String storageProvider = "FileStorageProvider";

    @Getter
    private String token = "TOKEN_HERE";

    @Getter
    private boolean ratelimit = true, maxRoleSize = true;

    @Getter
    private int ratelimitSeconds = 30;


    // --------------------------------------------------------------
    private static final File CONFIG_FILE = new File("config.json");
    private static Configuration instance;
    private Configuration() {
    }

    public static Configuration getInstance() {
        if (instance == null) {
            load();
        }
        return instance;
    }

    @SneakyThrows
    public static Configuration load() {
        if (CONFIG_FILE.exists()) {
            String json = new String(Files.readAllBytes(CONFIG_FILE.toPath()));
            System.out.println("JSON: " + json);
            return instance =  AntiStaffPing.getGson().fromJson(json, Configuration.class);
        } else {
            instance = new Configuration();
            Files.write(CONFIG_FILE.toPath(), AntiStaffPing.getGson().toJson(instance).getBytes());
            return instance;
        }
    }

    private StorageProvider spInst;
    @SneakyThrows
    public StorageProvider getStorageProvider() {
        if (spInst != null) return spInst;
        String packageName = StorageProvider.class.getPackage().getName() + ".impl.";
        return spInst = (StorageProvider) Class.forName(packageName + storageProvider).newInstance();
    }

    @Override
    public String toString() {
        return "Configuration{" +
                "storageProvider='" + storageProvider + '\'' +
                ", token='" + token + '\'' +
                ", ratelimit=" + ratelimit +
                ", maxRoleSize=" + maxRoleSize +
                ", ratelimitSeconds=" + ratelimitSeconds +
                ", spInst=" + spInst +
                '}';
    }
}
