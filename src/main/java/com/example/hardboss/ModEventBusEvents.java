package com.example.hardboss;

import com.example.hardboss.entity.HardBossEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "hardboss", bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBusEvents {

    @SubscribeEvent
    public static void entityAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.HARD_BOSS.get(), Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 100.0)
            .add(Attributes.ATTACK_DAMAGE, 10.0)
            .build());
    }
}