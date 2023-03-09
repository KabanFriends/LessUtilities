package io.github.kabanfriends.lessutilities.config;

import io.github.kabanfriends.lessutilities.LessUtilities;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;

@me.shedaniel.autoconfig.annotation.Config(name = LessUtilities.MOD_ID)
public class Config implements ConfigData {

    public boolean useSideChat = true;
    public int sideChatWidth = 0;

    public static Config getConfig() {
        return AutoConfig.getConfigHolder(Config.class).getConfig();
    }
}
