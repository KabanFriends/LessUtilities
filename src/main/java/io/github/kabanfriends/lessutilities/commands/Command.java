package io.github.kabanfriends.lessutilities.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.Minecraft;

public abstract class Command {
    public abstract void register(Minecraft mc, CommandDispatcher<FabricClientCommandSource> cd);
}