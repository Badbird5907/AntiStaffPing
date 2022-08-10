package dev.badbird.antistaffping.commands.impl;

import dev.badbird.antistaffping.objects.Configuration;
import dev.badbird.antistaffping.objects.ServerConfig;
import net.badbird5907.jdacommand.annotation.Command;
import net.badbird5907.jdacommand.annotation.Required;
import net.badbird5907.jdacommand.context.CommandContext;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.time.Duration;

public class SettingsCommands {
    @Command(name = "settings deleteOriginalMessage", description = "Should the original message be deleted?", permission = Permission.MANAGE_SERVER)
    public void settings(CommandContext context, boolean delete, ServerConfig serverConfig) {
        OptionMapping option = context.getOption("delete");
        if (option != null) {
            serverConfig.setDeleteOriginalMessage(delete);
            Configuration.getInstance().getStorageProvider().save(serverConfig);
            context.reply("Set `DeleteOriginalMessage` to " + delete);
        } else {
            context.reply("Delete Original Message is currently set to `" + serverConfig.isDeleteOriginalMessage() + "`");
        }
    }

    @Command(name = "settings deleteReplyMessage", description = "Should the bot reply message be deleted?", permission = Permission.MANAGE_SERVER)
    public void delSentMessage(CommandContext context, @Required boolean delete, ServerConfig serverConfig) {
        OptionMapping option = context.getOption("delete");
        if (option != null) {
            serverConfig.setDeleteReplyMessage(delete);
            Configuration.getInstance().getStorageProvider().save(serverConfig);
            context.reply("Set `DeleteReplyMessage` to " + delete);
        } else {
            context.reply("Delete Reply Message is currently set to `" + serverConfig.isDeleteReplyMessage() + "`");
        }
    }

    @Command(name = "settings timeoutUser", description = "Should the user be timed out?", permission = Permission.MANAGE_SERVER)
    public void timeoutUser(CommandContext context, @Required boolean timeout, ServerConfig serverConfig) {
        OptionMapping option = context.getOption("timeout");
        if (option != null) {
            serverConfig.setTimeoutUser(timeout);
            Configuration.getInstance().getStorageProvider().save(serverConfig);
            context.reply("Set `TimeoutUser` to " + timeout);
        } else {
            context.reply("Timeout User is currently set to `" + serverConfig.isTimeoutUser() + "`");
        }
    }

    @Command(name = "settings deleteMessageDelay", description = "How long should the bot wait before deleting the reply message? (deleteReplyMessage has to be on)", permission = Permission.MANAGE_SERVER)
    public void deleteMessageDelay(CommandContext context, @Required int seconds, ServerConfig serverConfig) {
        OptionMapping option = context.getOption("seconds");
        if (option != null) {
            serverConfig.setDeleteMessageDelay(Duration.ofSeconds(seconds).toMillis());
            Configuration.getInstance().getStorageProvider().save(serverConfig);
            context.reply("Set `DeleteMessageDelay` to " + seconds + " seconds");
        } else {
            context.reply("Delete Message Delay is currently set to `" + serverConfig.getDeleteMessageDelay() + " seconds`");
        }
    }

    @Command(name = "settings timeoutDuration", description = "How long should the user be timed out? (timeoutUser has to be on)", permission = Permission.MANAGE_SERVER)
    public void timeoutDuration(CommandContext context, @Required int seconds, ServerConfig serverConfig) {
        OptionMapping option = context.getOption("seconds");
        if (option != null) {
            serverConfig.setTimeoutDuration(Duration.ofSeconds(seconds).toMillis());
            Configuration.getInstance().getStorageProvider().save(serverConfig);
            context.reply("Set `TimeoutDuration` to " + seconds + " seconds");
        } else {
            context.reply("Timeout Duration is currently set to `" + serverConfig.getTimeoutDuration() + " seconds`");
        }
    }

    @Command(name = "settings lastMessagedRequirement", description = "Threshold for the minutes since the last time the mentioned user sent a message", permission = Permission.MANAGE_SERVER) // TODO come up with a better description
    public void lastMessagedRequirement(CommandContext context, @Required int minutes, ServerConfig serverConfig) {
        OptionMapping option = context.getOption("minutes");
        if (option != null) {
            serverConfig.setLastMessagedRequirement(minutes);
            Configuration.getInstance().getStorageProvider().save(serverConfig);
            context.reply("Set `LastMessagedRequirement` to " + minutes + " minutes");
        } else {
            context.reply("Last Messaged Requirement is currently set to `" + serverConfig.getLastMessagedRequirement() + " minutes`");
        }
    }

    @Command(name = "settings message", description = "The message that the bot should reply with", permission = Permission.MANAGE_SERVER)
    public void message(CommandContext context, @Required String message, ServerConfig serverConfig) {
        OptionMapping option = context.getOption("message");
        if (option != null) {
            serverConfig.setMessage(message);
            Configuration.getInstance().getStorageProvider().save(serverConfig);
            context.reply("Set `Message` to " + message);
        } else {
            context.reply("Message is currently set to `" + serverConfig.getMessage() + "` <user> is replaced with a mention of the sender.");
        }
    }
}
