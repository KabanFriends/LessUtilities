package io.github.kabanfriends.lessutilities.websocket.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.kabanfriends.lessutilities.LessUtilities;
import io.github.kabanfriends.lessutilities.utils.ItemUtils;
import io.github.kabanfriends.lessutilities.utils.ToasterUtils;
import io.github.kabanfriends.lessutilities.websocket.SocketHandler;
import io.github.kabanfriends.lessutilities.websocket.client.type.SocketItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.SoundEvents;

@Environment(EnvType.CLIENT)
public class Clients {

    public static String acceptData(String line) {
        JsonObject result = new JsonObject();
        try {
            if (line == null) {
                return null;
            }
            LocalPlayer player = LessUtilities.MC.player;

            JsonObject data = JsonParser.parseString(line).getAsJsonObject();
            String type = data.get("type").getAsString();
            String itemData = data.get("data").getAsString();
            String source = data.get("source").getAsString();

            SocketItem item = SocketHandler.getInstance().getSocketItems().get(type);
            if (item == null) {
                throw new IllegalArgumentException("Could not find an item type that matched " + type + "!");
            }
            if (player == null) {
                throw new Exception("Player is not logged in!");
            }

            if (player.isCreative()) {
                ItemUtils.giveCreativeItem(item.getItem(itemData), true);
                ToasterUtils.sendToaster("Received Item!", source, SystemToast.SystemToastId.NARRATOR_TOGGLE);
                player.playSound(SoundEvents.ITEM_PICKUP, 200, 1);
                result.addProperty("status", "success");
            } else {
                throw new Exception("Player is not in creative!");
            }
        } catch (Throwable e) {
            result.addProperty("status", "error");
            result.addProperty("error", e.getMessage());
        }

        return result.toString();
    }

}