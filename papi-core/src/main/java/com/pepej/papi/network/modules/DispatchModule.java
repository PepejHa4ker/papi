package com.pepej.papi.network.modules;

import com.pepej.papi.command.Commands;
import com.pepej.papi.Papi;
import com.pepej.papi.scheduler.Schedulers;
import com.pepej.papi.messaging.InstanceData;
import com.pepej.papi.messaging.Messenger;
import com.pepej.papi.messaging.conversation.ConversationChannel;
import com.pepej.papi.messaging.conversation.ConversationMessage;
import com.pepej.papi.messaging.conversation.ConversationReply;
import com.pepej.papi.messaging.conversation.ConversationReplyListener;
import com.pepej.papi.terminable.TerminableConsumer;
import com.pepej.papi.terminable.module.TerminableModule;
import com.pepej.papi.utils.Players;
import org.bukkit.command.CommandException;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class DispatchModule implements TerminableModule {
    private final Messenger messenger;
    private final InstanceData instanceData;
    private final String[] commandAliases;

    public DispatchModule(Messenger messenger, InstanceData instanceData) {
        this(messenger, instanceData, new String[]{"dispatch"});
    }

    public DispatchModule(Messenger messenger, InstanceData instanceData, String[] commandAliases) {
        this.messenger = messenger;
        this.instanceData = instanceData;
        this.commandAliases = commandAliases;
    }

    @Override
    public void setup(@NonNull TerminableConsumer consumer) {
        ConversationChannel<DispatchMessage, DispatchReply> dispatchChannel = this.messenger.getConversationChannel("papi-dispatch", DispatchMessage.class, DispatchReply.class);

        // listen for dispatches targeting this server
        dispatchChannel.newAgent((agent, message) -> {
            // notify players with the permission that the dispatch took place
            Players.stream()
                   .filter(p -> p.hasPermission("papi.dispatchalert"))
                   .filter(p -> !p.getUniqueId().equals(message.senderUuid))
                   .forEach(p -> Players.msg(p, "&7[&anetwork&7] &2" + message.senderName + "&7 on &2" + message.senderLocation + "&7 dispatched command '&f" + message.command + "&7' to '&2" + message.target + "&7'."));

            if (!(message.target.equals("all") || this.instanceData.getGroups().contains(message.target) || this.instanceData.getId().equals(message.target))) {
                return ConversationReply.noReply();
            }

            return ConversationReply.ofPromise(Schedulers.sync().supply(() -> {
                boolean success = false;
                try {
                    success = Papi.server().dispatchCommand(Papi.console(), message.command);
                } catch (CommandException e) {
                    // ignore
                }

                DispatchReply reply = new DispatchReply();
                reply.convoId = message.convoId;
                reply.server = this.instanceData.getId();
                reply.success = success;
                return reply;
            }));
        }).bindWith(consumer);

        Commands.create()
                .assertPermission("papi.dispatch")
                .assertUsage("<target> <command>")
                .handler(c -> {
                    String target = c.arg(0).parseOrFail(String.class).toLowerCase();
                    String command = c.args().stream().skip(1).collect(Collectors.joining(" "));

                    DispatchMessage dispatch = new DispatchMessage();
                    dispatch.convoId = UUID.randomUUID();
                    dispatch.command = command;
                    dispatch.target = target;
                    if (c.sender() instanceof Player) {
                        dispatch.senderUuid = ((Player) c.sender()).getUniqueId();
                    }
                    dispatch.senderName = c.sender().getName();
                    dispatch.senderLocation = this.instanceData.getId();

                    dispatchChannel.sendMessage(dispatch, new ConversationReplyListener<DispatchReply>() {
                        @NonNull
                        @Override
                        public RegistrationAction onReply(@NonNull DispatchReply reply) {
                            if (reply.success) {
                                Players.msg(c.sender(), "&7[&anetwork&7] Dispatched command '&f" + command + "&7' was &asuccessfully executed&7 on &2" + reply.server + "&7.");
                            } else {
                                Players.msg(c.sender(), "&7[&anetwork&7] Dispatched command '&f" + command + "&7' could &cnot be successfully executed&7 on &2" + reply.server + "&7.");
                            }
                            return RegistrationAction.CONTINUE_LISTENING;
                        }

                        @Override
                        public void onTimeout(@NonNull List<DispatchReply> replies) {
                            if (replies.isEmpty()) {
                                Players.msg(c.sender(), "&7[&anetwork&7] Dispatched command '&f" + command + "&7' was not acknowledged by any servers.");
                            }
                        }
                    }, 3, TimeUnit.SECONDS);

                    Players.msg(c.sender(), "&7[&anetwork&7] Dispatched command '&f" + command + "&7' to '&2" + target + "&7'.");
                })
                .registerAndBind(consumer, this.commandAliases);
    }

    private static final class DispatchMessage implements ConversationMessage {
        private UUID convoId;

        private String command;
        private String target;
        private UUID senderUuid;
        private String senderName;
        private String senderLocation;

        @NonNull
        @Override
        public UUID getConversationId() {
            return this.convoId;
        }
    }

    private static final class DispatchReply implements ConversationMessage {
        private UUID convoId;
        private String server;
        private boolean success;

        @NonNull
        @Override
        public UUID getConversationId() {
            return this.convoId;
        }
    }
}