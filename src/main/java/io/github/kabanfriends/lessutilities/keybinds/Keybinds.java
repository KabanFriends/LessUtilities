package io.github.kabanfriends.lessutilities.keybinds;

import io.github.kabanfriends.lessutilities.LessUtilities;
import io.github.kabanfriends.lessutilities.sidedchat.ChatShortcut;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.ChatScreen;

import java.util.Optional;

public class Keybinds implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        for (ChatShortcut chatShortcut: ChatShortcut.values()) {
            ChatShortcut.addKeyMapping(KeyBindingHelper.registerKeyBinding(new KeyMapping(
                    chatShortcut.getTranslationKey(), -1, "key.category.lessutilities"
            )), chatShortcut);
        }

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // chat shortcuts
            Optional<KeyMapping> pressedChatShortcut = ChatShortcut.keyMappings().stream()
                    .filter(keyBinding -> {
                        // filter which also needs to consume all of the wasPressed
                        // e.g. if multiple inputs went by before next frame was drawn
                        boolean pressed = false;
                        while (keyBinding.consumeClick()) {
                            pressed = true;
                        }
                        return pressed;
                    })
                    // will only handle the first, if for some reason you bind multiple chats to one button
                    .findFirst();

            // if any chat shortcut was pressed
            if (pressedChatShortcut.isPresent()) {
                ChatShortcut chatShortcut = ChatShortcut.getFromKey(pressedChatShortcut.get());

                ChatShortcut.setCurrentChatShortcut(chatShortcut);
                LessUtilities.MC.setScreen(new ChatScreen(""));
            }
        });
    }
}
