package io.github.kabanfriends.lessutilities.commands;

import com.mojang.brigadier.CommandDispatcher;
import io.github.kabanfriends.lessutilities.LessUtilities;
import io.github.kabanfriends.lessutilities.commands.impl.NBSCommand;
import io.github.kabanfriends.lessutilities.commands.impl.SchemCommand;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import java.util.ArrayList;
import java.util.List;

public class CommandHandler {

    private CommandDispatcher<FabricClientCommandSource> dispatcher;
    private List<Command> cmds = new ArrayList<>();

    public List<Command> getCommands() {
        return cmds;
    }

    public CommandHandler(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        this.dispatcher = dispatcher;
        register(
                new NBSCommand(),
                new SchemCommand()
        );
    }

    public void register(Command cmd) {
        cmd.register(LessUtilities.MC, this.dispatcher);
        cmds.add(cmd);
    }

    public void register(Command... cmds) {
        for (Command cmd : cmds) {
            this.register(cmd);
        }
    }
}