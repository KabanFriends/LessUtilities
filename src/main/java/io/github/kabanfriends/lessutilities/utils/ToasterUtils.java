package io.github.kabanfriends.lessutilities.utils;

import io.github.kabanfriends.lessutilities.LessUtilities;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class ToasterUtils {

    public static void sendToaster(String title, String description, SystemToast.SystemToastIds type) {
        sendToaster(Component.literal(title), Component.literal(description), type);
    }

    public static void sendTranslateToaster(String titleIdentifier, String descIdentifier, SystemToast.SystemToastIds type) {
        sendToaster(Component.translatable(titleIdentifier), Component.translatable(descIdentifier), type);
    }

    public static void sendToaster(MutableComponent title, MutableComponent description, SystemToast.SystemToastIds type) {
        LessUtilities.MC.getToasts().addToast(new SystemToast(type, title, description));
    }

}
