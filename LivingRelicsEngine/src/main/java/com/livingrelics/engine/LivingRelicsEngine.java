package com.livingrelics.engine;

import com.livingrelics.engine.entity_extensions.EntityExtensionLoader;
import com.livingrelics.engine.entity_extensions.EntityExtensionEvents;
import com.livingrelics.engine.relics.RelicEvents;
import com.livingrelics.engine.relics.RelicRegistry;
import com.livingrelics.engine.worldgen.VivianiteWorldgen;


import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LivingRelicsEngine implements ModInitializer {
	public static final String MOD_ID = "living_relics_engine";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		System.out.println(
        	"Living Relics Engine initialized!"
    	);
		RelicRegistry.register();
    	RelicEvents.register();
    	ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			System.out.println("[Living Relics] SERVER_STARTED fired!");
        	EntityExtensionLoader.load(server.getResourceManager());
    	});
		EntityExtensionEvents.register();
		VivianiteWorldgen.register();
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
