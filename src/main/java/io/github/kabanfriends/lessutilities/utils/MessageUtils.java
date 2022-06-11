package io.github.kabanfriends.lessutilities.utils;

import io.github.kabanfriends.lessutilities.LessUtilities;
import net.minecraft.network.chat.Component;

public class MessageUtils {

    public static void sendMessage(String message) {
        if (LessUtilities.MC.player == null) return;
        LessUtilities.MC.player.displayClientMessage(Component.literal(message), false);
    }

    public static void sendTranslatableMessage(String key) {
        if (LessUtilities.MC.player == null) return;
        LessUtilities.MC.player.displayClientMessage(Component.translatable(key), false);
    }
}