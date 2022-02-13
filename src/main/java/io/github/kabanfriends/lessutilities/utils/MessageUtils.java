package io.github.kabanfriends.lessutilities.utils;

import io.github.kabanfriends.lessutilities.LessUtilities;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class MessageUtils {

    public static void sendMessage(String message) {
        if (LessUtilities.MC.player == null) return;
        LessUtilities.MC.player.displayClientMessage(new TextComponent(message), false);
    }

    public static void sendTranslatableMessage(String key) {
        if (LessUtilities.MC.player == null) return;
        LessUtilities.MC.player.displayClientMessage(new TranslatableComponent(key), false);
    }
}