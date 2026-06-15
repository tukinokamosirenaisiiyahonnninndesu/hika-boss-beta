package com.example.hardboss.client.renderer;

import com.example.hardboss.entity.HardBossEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class HardBossRenderer extends MobRenderer<HardBossEntity, HumanoidModel<HardBossEntity>> {

    public HardBossRenderer(EntityRendererProvider.Context context) {
        // ZombieModelLayers.ZOMBIE ではなく、最も標準的なレイヤーを指定
        super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.ZOMBIE)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(HardBossEntity entity) {
        // テクスチャへのパス (assets/hardboss/textures/entity/hard_boss.png にある必要があります)
        return new ResourceLocation("hardboss", "textures/entity/hard_boss.png");
    }
}