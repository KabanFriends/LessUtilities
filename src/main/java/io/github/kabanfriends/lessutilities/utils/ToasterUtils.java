package io.github.kabanfriends.lessutilities.utils;

import io.github.kabanfriends.lessutilities.LessUtilities;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class ToasterUtils {

    public static void sendToaster(String title, String description, SystemToast.SystemToastIds type) {
        sendToaster(new TextComponent(title), new TextComponent(description), type);
    }

    public static void sendTranslateToaster(String titleIdentifier, String descIdentifier, SystemToast.SystemToastIds type) {
        sendToaster(new TranslatableComponent(titleIdentifier), new TranslatableComponent(descIdentifier), type);
    }

    public static void sendToaster(MutableComponent title, MutableComponent description, SystemToast.SystemToastIds type) {
        LessUtilities.MC.getToasts().addToast(new SystemToast(type, title, description));
    }

}
