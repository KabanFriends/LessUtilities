package io.github.kabanfriends.lessutilities.websocket.client.type;

import net.minecraft.world.item.ItemStack;

public abstract class SocketItem {

    public abstract String getIdentifier();

    public abstract ItemStack getItem(String data) throws Exception;

}
