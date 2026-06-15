package com.example.hardboss.common.entity;

import com.example.hardboss.common.entity.ai.HardBossWizardGoal;
import com.example.hardboss.common.entity.ai.WizardCounterSpellGoal;
import com.gametechbc.traveloptics.init.TravelopticsSpells;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

import java.util.List;

public class HardBossEntity extends AbstractSpellCastingMob implements Enemy {
    private final ServerBossEvent bossEvent;

    public HardBossEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        this.xpReward = 50000;
        this.bossEvent = (ServerBossEvent) (new ServerBossEvent(
                Component.literal("Hikachan"), BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.NOTCHED_20))
                .setDarkenScreen(true);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new WizardCounterSpellGoal(this));

        this.goalSelector.addGoal(1, new HardBossWizardGoal(this)
                .setSpells(
                        List.of(
                                SpellRegistry.SUMMON_POLAR_BEAR_SPELL.get(),
                                SpellRegistry.SONIC_BOOM_SPELL.get(),
                                SpellRegistry.WITHER_SKULL_SPELL.get(),
                                SpellRegistry.FANG_STRIKE_SPELL.get(),
                                SpellRegistry.ELECTROCUTE_SPELL.get(),
                                SpellRegistry.LIGHTNING_BOLT_SPELL.get(),
                                SpellRegistry.SHOCKWAVE_SPELL.get(),
                                SpellRegistry.VOLT_STRIKE_SPELL.get(),
                                SpellRegistry.GUIDING_BOLT_SPELL.get(),
                                TravelopticsSpells.TSUNAMI_SPELL.get(),
                                TravelopticsSpells.HYDROSHOT_SPELL.get(),
                                TravelopticsSpells.DEATH_LASER_SPELL.get(),
                                TravelopticsSpells.CURSED_REVENANTS_SPELL.get(),
                                TravelopticsSpells.STELE_CASCADE_SPELL.get(),
                                TravelopticsSpells.MECHANIZED_PREDATOR_SPELL.get(),
                                TravelopticsSpells.CONJURE_VOID_TOMES_SPELL.get(),
                                TravelopticsSpells.THE_HOWLING_TEMPEST.get()
                        ),
                        List.of(
                                SpellRegistry.ABYSSAL_SHROUD_SPELL.get()
                        ),
                        List.of(
                                SpellRegistry.BLOOD_STEP_SPELL.get()
                        ),
                        List.of(
                                SpellRegistry.CHARGE_SPELL.get(),
                                SpellRegistry.HASTE_SPELL.get(),
                                SpellRegistry.OAKSKIN_SPELL.get(),
                                SpellRegistry.HEAL_SPELL.get()
                        )
                )
                .setSpellQuality(0.5f, 1.0f)
        );

        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 0.75, false));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Mob.class, 12.0f));

        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        ItemStack staff = new ItemStack(ItemRegistry.PYRIUM_STAFF.get());
        this.setItemSlot(EquipmentSlot.MAINHAND, staff);
    }

    @Override
    public void tick() {
        super.tick();
        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());

        if (this.level().isClientSide() && !this.hasEffect(MobEffectRegistry.TRUE_INVISIBILITY.get())) {
            for (int i = 0; i < 3; i++) {
                double y = this.getY() + (this.random.nextFloat() * this.getBbHeight());

                double x = this.getRandomX(0.6);
                double z = this.getRandomZ(0.6);

                this.level().addParticle(ParticleHelper.SOUL_FIRE, x, y, z, 0, 0.1, 0);
                this.level().addParticle(ParticleTypes.SMOKE, x, y, z, 0.0, 0.05, 0.0);

                this.level().addParticle(ParticleHelper.UNSTABLE_ENDER, x, y, z, (this.random.nextDouble() - 0.5) * 0.2, -0.2, (this.random.nextDouble() - 0.5) * 0.2);
            }
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.FOLLOW_RANGE, 64.0)
                .add(Attributes.MAX_HEALTH, 600.0)
                .add(Attributes.ATTACK_DAMAGE, 12.0)
                .add(Attributes.MOVEMENT_SPEED, 0.4)
                .add(Attributes.ARMOR, 18.0);
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

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, SpawnGroupData pSpawnData, CompoundTag pDataTag) {
        SpawnGroupData data = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);

        this.populateDefaultEquipmentSlots(this.random, pDifficulty);

        return data;
    }
}
