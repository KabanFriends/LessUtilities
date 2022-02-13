package io.github.kabanfriends.lessutilities.commands;

import io.github.kabanfriends.lessutilities.LessUtilities;
import io.github.kabanfriends.lessutilities.commands.impl.NBSCommand;
import io.github.kabanfriends.lessutilities.commands.impl.SchemCommand;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;

import java.util.ArrayList;
import java.util.List;

public class CommandHandler {

    private static List<Command> cmds = new ArrayList<>();

    public static List<Command> getCommands() {
        return cmds;
    }

    public CommandHandler() {
        register(
                new NBSCommand(),
                new SchemCommand()
        );
    }

    public void register(Command cmd) {
        cmd.register(LessUtilities.MC, ClientCommandManager.DISPATCHER);
        cmds.add(cmd);
    }

    public void register(Command... cmds) {
        for (Command cmd : cmds) {
            this.register(cmd);
        }
    }
}