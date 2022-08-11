package dev.badbird.antistaffping.listener;

import dev.badbird.antistaffping.AntiStaffPing;
import dev.badbird.antistaffping.objects.Configuration;
import dev.badbird.antistaffping.objects.ServerConfig;
import net.badbird5907.jdacommand.JDACommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.guild.GuildAvailableEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class MessageListener extends ListenerAdapter {
    private final Map<Long, Long> ratelimitMap = new ConcurrentHashMap<>();

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (!event.isFromGuild()) {
            return;
        }
        if (ratelimitMap.containsKey(event.getGuild().getIdLong())) {
            long lastReplied = ratelimitMap.get(event.getGuild().getIdLong());
            if (System.currentTimeMillis() - lastReplied < (Configuration.getInstance().getRatelimitSeconds() * 1000L)) {
                return;
            } else ratelimitMap.remove(event.getGuild().getIdLong());
        }
        if (event.getAuthor().isBot()) {
            return;
        }

        Guild guild = event.getGuild();
        ServerConfig cfg = Configuration.getInstance().getStorageProvider().get(guild.getIdLong());
        if (cfg == null) {
            return;
        }
        Member author = event.getMember();
        if (cfg.isExempt(Objects.requireNonNull(author).getIdLong())) {
            return;
        }
        if (event.getMessage().getMentions().getMembers().isEmpty() && event.getMessage().getMentions().getRoles().isEmpty()) {
            return;
        }
        cfg.shouldRespond(event.getMessage()).handle((bool, e) -> { // Not using handleAsync as i'm not sure if that would cause a memory leak.
            if (bool) {
                ratelimitMap.put(event.getGuild().getIdLong(), System.currentTimeMillis());
                if (cfg.isDeleteOriginalMessage()) {
                    event.getMessage().delete().queue();
                    event.getChannel().sendMessage(cfg.getMessage().replace("<user>", author.getAsMention())).queue(msg -> {
                        if (cfg.isDeleteReplyMessage()) {
                            msg.delete().queueAfter(cfg.getDeleteMessageDelay(), TimeUnit.MILLISECONDS);
                        }
                    });
                } else {
                    event.getMessage().reply(cfg.getMessage().replace("<user>", author.getAsMention())).queue(msg -> {
                        if (cfg.isDeleteReplyMessage()) {
                            msg.delete().queueAfter(cfg.getDeleteMessageDelay(), TimeUnit.MILLISECONDS);
                        }
                    });
                }

                if (cfg.isTimeoutUser()) {
                    event.getMember().timeoutFor(Duration.ofMillis(cfg.getTimeoutDuration())).queue();
                }
            }
            return null;
        });
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.isGuildCommand()) return; // JDACommand will handle this
        if (event.getName().equalsIgnoreCase("info")) {
            String version = getClass().getPackage().getImplementationVersion();
            if (version == null) version = "DEV";
            event.replyEmbeds(new EmbedBuilder()
                    .setTitle("Anti Staff Ping")
                    .setDescription("This bot tells users not to ping staff")
                    .addField(new MessageEmbed.Field("By", "Badbird5907#5907", true))
                    .addField(new MessageEmbed.Field("Github", "[Link](https://github.com/Badbird5907/AntiStaffPing)", true))
                    .addField(new MessageEmbed.Field("Version", version, true))
                    .addField(new MessageEmbed.Field("Servers", AntiStaffPing.getJda().getGuilds().size() + "", true))
                    .addField(new MessageEmbed.Field("Users", AntiStaffPing.getJda().getGuilds().stream().mapToInt(Guild::getMemberCount).sum() + "", true))
                    .setColor(Color.CYAN)
                    .build()).setEphemeral(true).queue();
        }
    }

    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        System.out.println("(1) Joined guild: " + event.getGuild().getName());
        AntiStaffPing.registerCommands();
    }

    @Override
    public void onGuildAvailable(@NotNull GuildAvailableEvent event) {
        System.out.println("(2) Guild available: " + event.getGuild().getName());
        AntiStaffPing.registerCommands();
    }
}
