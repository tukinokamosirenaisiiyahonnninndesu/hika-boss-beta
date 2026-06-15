package com.example.hardboss.setup;

import com.example.hardboss.common.entity.HardBossEntity;
import com.example.hardboss.registry.EntityRegistry;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonSetup {
    @SubscribeEvent
    public static void onAttribute(EntityAttributeCreationEvent event) {
        event.put(EntityRegistry.HARD_BOSS.get(), HardBossEntity.createAttributes().build());
    }
}
