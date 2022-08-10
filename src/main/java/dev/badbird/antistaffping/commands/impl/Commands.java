package dev.badbird.antistaffping.commands.impl;

import dev.badbird.antistaffping.objects.Configuration;
import dev.badbird.antistaffping.objects.ServerConfig;
import net.badbird5907.jdacommand.annotation.Command;
import net.badbird5907.jdacommand.annotation.Required;
import net.badbird5907.jdacommand.annotation.Sender;
import net.badbird5907.jdacommand.context.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.util.stream.Collectors;

public class Commands {
    @Command(name = "show", description = "Shows info about the no ping roles & users, and the exempted roles", permission = {Permission.MANAGE_SERVER})
    public void show(@Sender Member member, CommandContext ctx, ServerConfig cfg) {
        cfg.checkNull();
        Guild guild = member.getGuild();

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("Info")
                .setDescription("All the no ping roles, users, and the exempted roles")
                .addField("Roles", String.join(" ", cfg.getNoPingRoles().stream().map(l -> "<@&" + l + ">").collect(Collectors.toList())), false)
                .addField("Users", String.join(" ", cfg.getNoPingUsers().stream().map(l -> "<@" + l + ">").collect(Collectors.toList())), false)
                .addField("Exempted Roles", String.join(" ", cfg.getExemptRoles().stream().map(l -> "<@&" + l + ">").collect(Collectors.toList())), false)
                .setFooter("No Staff Ping Bot - By Badbird5907#5907");
        ctx.reply(embedBuilder.build());
    }

    @Command(name = "add", description = "Adds a role/user to the no ping roles", permission = {Permission.MANAGE_SERVER})
    public void add(@Sender Member member, CommandContext ctx, @Required IMentionable roleOrUser, ServerConfig cfg) {
        cfg.checkNull();
        if (roleOrUser instanceof Role) {
            if (cfg.getNoPingRoles().contains(roleOrUser.getIdLong())) {
                ctx.reply("This role is already in the no ping roles list!");
                return;
            }
            cfg.getNoPingRoles().add(roleOrUser.getIdLong());
            ctx.reply("Added " + roleOrUser.getAsMention() + " to the no ping roles list.");
        } else if (roleOrUser instanceof Member) {
            if (cfg.getNoPingUsers().contains(roleOrUser.getIdLong())) {
                ctx.reply("This user is already in the no ping users list!");
                return;
            }
            cfg.getNoPingUsers().add(roleOrUser.getIdLong());
            ctx.reply("Added " + roleOrUser.getAsMention() + " to the no ping users list.");
        } else {
            ctx.reply("This is not a role or user!");
        }
        Configuration.getInstance().getStorageProvider().save(cfg);
    }
    @Command(name = "remove", description = "Removes a role/user from the no ping roles", permission = {Permission.MANAGE_SERVER})
    public void remove(@Sender Member member, CommandContext ctx, @Required IMentionable roleOrUser, ServerConfig cfg) {
        cfg.checkNull();
        if (roleOrUser instanceof Role) {
            if (!cfg.getNoPingRoles().contains(roleOrUser.getIdLong())) {
                ctx.reply("This role is not in the no ping roles list!");
                return;
            }
            cfg.getNoPingRoles().remove(roleOrUser.getIdLong());
            ctx.reply("Removed " + roleOrUser.getAsMention() + " from the no ping roles list.");
        } else if (roleOrUser instanceof Member) {
            if (!cfg.getNoPingUsers().contains(roleOrUser.getIdLong())) {
                ctx.reply("This user is not in the no ping users list!");
                return;
            }
            cfg.getNoPingUsers().remove(roleOrUser.getIdLong());
            ctx.reply("Removed " + roleOrUser.getAsMention() + " from the no ping users list.");
        } else {
            ctx.reply("This is not a role or user!");
        }
        Configuration.getInstance().getStorageProvider().save(cfg);
    }

}
