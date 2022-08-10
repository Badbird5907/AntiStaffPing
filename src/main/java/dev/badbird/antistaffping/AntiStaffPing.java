package dev.badbird.antistaffping;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.badbird.antistaffping.commands.impl.Commands;
import dev.badbird.antistaffping.commands.impl.SettingsCommands;
import dev.badbird.antistaffping.commands.provider.ServerConfigProvider;
import dev.badbird.antistaffping.listener.MessageListener;
import dev.badbird.antistaffping.objects.Configuration;
import lombok.Getter;
import lombok.SneakyThrows;
import net.badbird5907.jdacommand.JDACommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class AntiStaffPing {
    @Getter
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Getter
    private static JDA jda;

    @Getter
    private static final ScheduledExecutorService threadPool = Executors.newSingleThreadScheduledExecutor();

    @SneakyThrows
    public static void main(String[] args) {
        Configuration.load();
        System.out.println("Loaded config: " + Configuration.getInstance());
        jda = JDABuilder.createDefault(Configuration.getInstance().getToken())
                .enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES)
                .addEventListeners(new MessageListener())
                .build().awaitReady();
        JDACommand command = new JDACommand(jda); //TODO finish commands
        command.registerProvider(new ServerConfigProvider());
        command.registerCommand(new Commands());
        command.registerCommand(new SettingsCommands());
        //command.registerCommandsInPackage("dev.badbird.antistaffping.commands.impl");

        //enable the slash commands badge
        jda.upsertCommand("info", "Info about the bot").queue();

        Configuration.getInstance().getStorageProvider().init();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Configuration.getInstance().getStorageProvider().disable();
        }, "Shutdown Thread"));

    }
}
