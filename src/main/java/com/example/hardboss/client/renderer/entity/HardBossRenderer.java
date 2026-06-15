package com.example.hardboss.client.renderer.entity;

import com.example.hardboss.client.model.entity.HardBossModel;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class HardBossRenderer extends AbstractSpellCastingMobRenderer {
    public HardBossRenderer(EntityRendererProvider.Context context) {
        super(context, new HardBossModel());
    }
}
