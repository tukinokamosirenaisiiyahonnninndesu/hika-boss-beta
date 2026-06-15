package com.example.hardboss.registry;

import com.example.hardboss.HardBoss;
import com.example.hardboss.common.entity.HardBossEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EntityRegistry {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, HardBoss.MOD_ID);
    public static void register(IEventBus eventBus) { ENTITIES.register(eventBus); }

    public static final RegistryObject<EntityType<HardBossEntity>> HARD_BOSS = ENTITIES.register("hard_boss", () -> EntityType.Builder.of(HardBossEntity::new, MobCategory.MONSTER)
            .sized(0.6f, 1.95f)
            .build(HardBoss.id("hard_boss").toString()));
}
