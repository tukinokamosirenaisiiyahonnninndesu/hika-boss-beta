package com.example.hardboss;

import com.example.hardboss.entity.ModEntities;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("hardboss")
public class HardBossMod {
    public HardBossMod() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        
        // ここでModEntitiesを正しく呼び出す
        ModEntities.register(bus);
    }
}