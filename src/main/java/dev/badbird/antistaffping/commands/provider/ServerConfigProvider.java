package dev.badbird.antistaffping.commands.provider;

import dev.badbird.antistaffping.objects.Configuration;
import dev.badbird.antistaffping.objects.ServerConfig;
import net.badbird5907.jdacommand.context.CommandContext;
import net.badbird5907.jdacommand.context.ParameterContext;
import net.badbird5907.jdacommand.provider.Provider;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class ServerConfigProvider implements Provider<ServerConfig> {
    @Override
    public ServerConfig provide(CommandContext context, ParameterContext pContext) {
        ServerConfig serverConfig = Configuration.getInstance().getStorageProvider().get(context.getGuild().getIdLong());
        System.out.println("CFG: " + serverConfig);
        return serverConfig;
    }

    @Override
    public OptionData getOptionData(ParameterContext paramContext) {
        return null;
    }

    @Override
    public Class<?> getType() {
        return ServerConfig.class;
    }
}
