package io.github.kabanfriends.lessutilities.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.kabanfriends.lessutilities.LessUtilities;
import io.github.kabanfriends.lessutilities.screen.CPUUsageText;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundResourcePackPacket;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class MixinClientPacketListener {

    @Unique
    private static final String DEFAULT_PACK_SHA1 = "8889a2049b89ee3ee85b8b9a505a197094bcde91";

    @Inject(method = "handleSystemChat", at = @At("HEAD"))
    public void lessutilities$onChatMessage(ClientboundSystemChatPacket packet, CallbackInfo ci) {
        if (!RenderSystem.isOnRenderThread()) {
            return;
        }

        Component component = packet.content();
        String text = component.getString();

        if (text.startsWith("⏵ Click to edit variable: ")) {
            ClickEvent event = component.getStyle().getClickEvent();
            if (event != null && event.getAction() == ClickEvent.Action.SUGGEST_COMMAND) {
                String content = component.getStyle().getClickEvent().getValue();
                LessUtilities.MC.setScreen(new ChatScreen(content));
            }
        }
    }

    @Inject(method = "setActionBarText", at = @At("HEAD"), cancellable = true)
    public void lessutilities$onActionBar(ClientboundSetActionBarTextPacket packet, CallbackInfo ci) {
        if (!RenderSystem.isOnRenderThread()) {
            return;
        }
        if (LessUtilities.MC.player == null) {
            return;
        }
        if (packet.getText().getString().matches("^CPU Usage: \\[▮▮▮▮▮▮▮▮▮▮▮▮▮▮▮▮▮▮▮▮\\] \\(.*%\\)$")) {
            CPUUsageText.updateCPU(packet);
            ci.cancel();
        }
    }

    @Inject(method = "handleResourcePack", at = @At("HEAD"), cancellable = true)
    public void lessutilities$resetServerPack(ClientboundResourcePackPacket packet, CallbackInfo ci) {
        if (true) return; //FIXME: this does not work
        if (packet.getHash().equals(DEFAULT_PACK_SHA1)) {
            LessUtilities.MC.getDownloadedPackSource().clearServerPack();
            ci.cancel();
        }
    }
}
