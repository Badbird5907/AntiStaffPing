package dev.badbird.antistaffping.objects;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Mentions;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Getter
@Setter
@RequiredArgsConstructor
public class ServerConfig {
    private final long serverId;

    private List<Long> noPingRoles = new ArrayList<>();
    private List<Long> noPingUsers = new ArrayList<>();
    private List<Long> exemptRoles = new ArrayList<>();
    private String message = "<user> Please do not ping staff members.";
    private boolean deleteOriginalMessage = false, deleteReplyMessage = false, timeoutUser = false;
    private long deleteMessageDelay = Duration.ofSeconds(10).toMillis(), timeoutDuration = Duration.ofMinutes(5).toMillis(), lastMessagedRequirement = Duration.ofMinutes(2).toMillis();

    private long maxRoleSize = 20;

    public void checkNull() { // Thanks gson...
        if (message == null) {
            message = "<user> Please do not ping staff members.";
        }
        if (noPingUsers == null) {
            noPingUsers = new ArrayList<>();
        }
        if (noPingRoles == null) {
            noPingRoles = new ArrayList<>();
        }
        if (exemptRoles == null) {
            exemptRoles = new ArrayList<>();
        }
    }

    public boolean isExempt(long userId) {
        return exemptRoles.contains(userId);
    }

    public CompletableFuture<Boolean> shouldRespond(Message message) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        Mentions mentions = message.getMentions();
        for (Member member : mentions.getMembers()) {
            if (noPingUsers.contains(member.getIdLong())) {
                try {
                    return future;
                } finally {
                    future.complete(true);
                }
            }
            List<Long> roles = member.getRoles().stream().map(Role::getIdLong).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
            boolean checkRoles = false;
            for (Long role : roles) {
                if (noPingRoles.contains(role)) {
                    checkRoles = true;
                    break;
                }
            }
            if (checkRoles) {
                message.getChannel().getHistoryBefore(message, 50).queue(result -> {
                    long lastActivity = -1;
                    for (Message msg : result.getRetrievedHistory()) {
                        if (msg.getAuthor().getIdLong() == member.getIdLong()) {
                            lastActivity = (msg.getTimeCreated().toInstant().toEpochMilli());
                            break;
                        }
                    }
                    if (lastActivity == -1) {
                        future.complete(true);
                    } else if (System.currentTimeMillis() - lastActivity > lastMessagedRequirement) { // If the user has not been messaged in the last X minutes, yes.
                        future.complete(true);
                    } else {
                        future.complete(false);
                    }
                });
            }
        }
        for (Role role : mentions.getRoles()) {
            if (noPingRoles.contains(role.getIdLong())) {
                try {
                    return future;
                } finally {
                    future.complete(true);
                }
            }
        }
        return future;
    }


    @Override
    public String toString() {
        return "ServerConfig{" +
                "serverId=" + serverId +
                ", noPingRoles=" + noPingRoles +
                ", noPingUsers=" + noPingUsers +
                ", exemptRoles=" + exemptRoles +
                ", message='" + message + '\'' +
                ", deleteOriginalMessage=" + deleteOriginalMessage +
                ", deleteSentMessage=" + deleteReplyMessage +
                ", timeoutUser=" + timeoutUser +
                ", deleteMessageDelay=" + deleteMessageDelay +
                ", timeoutTime=" + timeoutDuration +
                ", lastMessagedRequirement=" + lastMessagedRequirement +
                ", maxRoleSize=" + maxRoleSize +
                '}';
    }
}
