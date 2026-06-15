package com.example.hardboss.entity;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.util.RandomSource;

public class HardBossEntity extends Monster {
    private final ServerBossEvent bossEvent;

    public HardBossEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 50000;
        this.bossEvent = (ServerBossEvent) (new ServerBossEvent(
                Component.literal("Hikachan"), BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.NOTCHED_20))
                .setDarkenScreen(true);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new net.minecraft.world.entity.ai.goal.FloatGoal(this));
        this.goalSelector.addGoal(2, new net.minecraft.world.entity.ai.goal.MeleeAttackGoal(this, 1.2D, false));
        this.goalSelector.addGoal(3, new net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new net.minecraft.world.entity.ai.goal.LookAtPlayerGoal(this, Player.class, 8.0F));
        this.targetSelector.addGoal(1, new net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(2, new net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal(this));
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, net.minecraft.world.DifficultyInstance difficulty) {
        ItemStack sword = new ItemStack(Items.DIAMOND_SWORD);
        sword.enchant(Enchantments.SHARPNESS, 5);
        this.setItemSlot(EquipmentSlot.MAINHAND, sword);
    }

    // ステータス設定：HP 3000, 防御 20
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 3000.0D)
                .add(Attributes.ATTACK_DAMAGE, 45.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.42D)
                .add(Attributes.ARMOR, 20.0D);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());

        boolean isEnraged = this.getHealth() <= 1500.0F;

        if (this.tickCount % 20 == 0) {
            float healAmount = isEnraged ? 5.0F : 2.0F;
            this.heal(healAmount);
            
            this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(10.0D))
                .forEach(player -> player.addEffect(new MobEffectInstance(MobEffects.WITHER, 100, 1)));
        }

        LivingEntity target = this.getTarget();
        if (target != null) {
            if (isEnraged) {
                this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 40, 1));
            }

            if (this.tickCount % 100 == 0) {
                this.teleportTo(target.getX() + (random.nextDouble() - 0.5) * 5, target.getY(), target.getZ() + (random.nextDouble() - 0.5) * 5);
            }
            if (this.tickCount % 80 == 0 && this.level() instanceof ServerLevel sl) {
                EntityType.LIGHTNING_BOLT.spawn(sl, target.blockPosition(), MobSpawnType.TRIGGERED);
            }
            if (this.tickCount % 60 == 0 && this.distanceToSqr(target) < 225.0D) {
                target.hurt(this.damageSources().sonicBoom(this), isEnraged ? 30.0F : 20.0F);
            }
            if (this.tickCount % 200 == 0) {
                summonMinions();
            }
        }
    }

    private void summonMinions() {
        if (this.level().isClientSide) return;
        EntityType<?>[] types = {
            EntityType.ZOMBIE, 
            EntityType.SKELETON, 
            EntityType.CREEPER, 
            EntityType.SPIDER, 
            EntityType.WITHER_SKELETON, 
            EntityType.BLAZE
        };

        for (int i = 0; i < 10; i++) {
            Entity minion = types[this.random.nextInt(types.length)].create(this.level());
            if (minion instanceof Mob mob) {
                mob.moveTo(this.getX() + (random.nextDouble() - 0.5) * 4, this.getY(), this.getZ() + (random.nextDouble() - 0.5) * 4);
                mob.setPersistenceRequired();
                if (minion instanceof Monster monster && this.getTarget() != null) {
                    monster.setTarget(this.getTarget());
                }
                this.level().addFreshEntity(mob);
            }
        }
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossEvent.removePlayer(player);
    }
}