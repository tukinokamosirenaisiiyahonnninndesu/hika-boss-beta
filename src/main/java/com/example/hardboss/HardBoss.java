package com.example.hardboss;

import com.example.hardboss.registry.EntityRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(HardBoss.MOD_ID)
public class HardBoss {
    public static final String MOD_ID = "hardboss";

    public HardBoss() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        EntityRegistry.register(eventBus);
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
