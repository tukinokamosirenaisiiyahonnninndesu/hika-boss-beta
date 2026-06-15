package com.example.hardboss;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("hardboss")
public class HardBossMod {
    public HardBossMod() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        
        // ModEntitiesを呼び出して登録
        ModEntities.register(bus);
        
        MinecraftForge.EVENT_BUS.register(this);
    }
}