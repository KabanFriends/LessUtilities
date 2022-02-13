package io.github.kabanfriends.lessutilities.mixin;

import io.github.kabanfriends.lessutilities.sidedchat.ChatShortcut;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    @Inject(method = "openChatScreen", at = @At("HEAD"))
    public void openChatScreen(String text, CallbackInfo ci) {
        // set such that no shortcut is active when pressing 't'
        ChatShortcut.setCurrentChatShortcut(null);
    }
}
