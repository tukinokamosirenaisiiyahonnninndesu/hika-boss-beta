package com.example.hardboss.client;

import com.example.hardboss.ModEntities;
import com.example.hardboss.client.renderer.HardBossRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers; // 追加
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "hardboss", bus = Mod.EventBusSubscriber.Bus.MOD)
public class HardBossClientEvents {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // ここを汎用的な登録方法に変更します
        event.registerEntityRenderer(ModEntities.HARD_BOSS.get(), HardBossRenderer::new);
    }
}