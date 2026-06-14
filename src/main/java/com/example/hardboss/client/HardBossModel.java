package com.example.hardboss.client;

import com.example.hardboss.entity.HardBossEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;

public class HardBossModel extends HumanoidModel<HardBossEntity> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            new ResourceLocation("hardboss", "hard_boss"), "main");

    public HardBossModel(ModelPart root) {
        super(root);
    }
}