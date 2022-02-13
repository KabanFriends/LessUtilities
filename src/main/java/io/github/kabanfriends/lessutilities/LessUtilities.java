package io.github.kabanfriends.lessutilities;

import io.github.kabanfriends.lessutilities.commands.CommandHandler;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.Minecraft;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LessUtilities implements ModInitializer {

	public static final String MOD_ID = "lessutilities";
	public static final String MOD_NAME = "LessUtilities";

	public static final Minecraft MC = Minecraft.getInstance();
	public static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing LessUtilities...");

		new CommandHandler();
	}
}
