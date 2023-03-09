package io.github.kabanfriends.lessutilities.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.kabanfriends.lessutilities.sidedchat.ChatShortcut;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatScreen.class)
public class MixinChatScreen {

    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/ChatScreen;fill(Lcom/mojang/blaze3d/vertex/PoseStack;IIIII)V"), index = 5)
    private int getTextboxColour(int defaultColour) {
        ChatShortcut currentChatShortcut = ChatShortcut.getCurrentChatShortcut();

        // if there is one active - use it
        if (currentChatShortcut != null) {
            return currentChatShortcut.getColor().getRGB();
        }
        // else use the default minecraft option
        else return defaultColour;
    }

    @ModifyArg(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/ChatScreen;handleChatInput(Ljava/lang/String;Z)Z"), index = 0)
    private String insertPrefix(String interceptedMessage) {
        ChatShortcut currentChatShortcut = ChatShortcut.getCurrentChatShortcut();

        if (currentChatShortcut != null) {
            // the prefix already includes the space
            return currentChatShortcut.getPrefix() + interceptedMessage;
        }
        // else just send the message
        else return interceptedMessage;
    }
}