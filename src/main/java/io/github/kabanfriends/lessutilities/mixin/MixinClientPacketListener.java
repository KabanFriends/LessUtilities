package io.github.kabanfriends.lessutilities.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.kabanfriends.lessutilities.LessUtilities;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundChatPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class MixinClientPacketListener {

    @Inject(method = "handleChat", at = @At("HEAD"))
    public void onChatMessage(ClientboundChatPacket packet, CallbackInfo ci) {
        if (!RenderSystem.isOnRenderThread()) {
            return;
        }
        if (packet.getType() == ChatType.CHAT || packet.getType() == ChatType.SYSTEM) {
            Component component = packet.getMessage();
            String text = component.getString();

            LessUtilities.LOGGER.info(text);
            if (text.startsWith("⏵ Click to edit variable: ")) {
                if (component.getStyle().getClickEvent().getAction() == ClickEvent.Action.SUGGEST_COMMAND) {
                    String content = component.getStyle().getClickEvent().getValue();
                    LessUtilities.MC.setScreen(new ChatScreen(content));
                }
            }
        }
    }
}
