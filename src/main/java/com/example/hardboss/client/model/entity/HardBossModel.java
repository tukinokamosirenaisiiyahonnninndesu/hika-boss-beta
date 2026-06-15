package com.example.hardboss.client.model.entity;

import com.example.hardboss.HardBoss;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMobModel;
import net.minecraft.resources.ResourceLocation;

public class HardBossModel extends AbstractSpellCastingMobModel {
    private static final ResourceLocation TEXTURE = HardBoss.id("textures/entity/hard_boss.png");

    @Override
    public ResourceLocation getTextureResource(AbstractSpellCastingMob mob) {
        return TEXTURE;
    }
}