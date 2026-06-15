package com.example.hardboss.common.entity.ai;

import io.redspace.ironsspellbooks.api.entity.IMagicEntity;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.capabilities.magic.SummonedEntitiesCastData;
import io.redspace.ironsspellbooks.entity.mobs.goals.WizardAttackGoal;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HardBossWizardGoal extends WizardAttackGoal {
    protected final Map<AbstractSpell, Integer> spellCooldowns = new HashMap<>();

    protected AbstractSpell currentRecastSpell = SpellRegistry.none();
    protected int remainingRecastCount = 0;
    protected int currentRecastLevel = 1;

    public HardBossWizardGoal(IMagicEntity abstractSpellCastingMob) {
        super(abstractSpellCastingMob, 0.75, 40, 80);
    }

    @Override
    public void stop() {
        super.stop();
        this.currentRecastSpell = SpellRegistry.none();
        this.remainingRecastCount = 0;
    }

    @Override
    protected AbstractSpell getNextSpellType() {
        if ((this.target instanceof IMagicEntity magicTarget && magicTarget.isCasting()) || (this.target instanceof Player player && MagicData.getPlayerMagicData(player).isCasting())) {
            AbstractSpell counterSpell = SpellRegistry.COUNTERSPELL_SPELL.get();
            int currentTick = this.mob.tickCount;

            if (this.spellCooldowns.getOrDefault(counterSpell, 0) <= currentTick) {
                this.spellCooldowns.put(counterSpell, currentTick + counterSpell.getSpellCooldown());

                this.currentRecastSpell = SpellRegistry.none();
                this.remainingRecastCount = 0;

                return counterSpell;
            }
        }

        if (this.remainingRecastCount > 0 && this.currentRecastSpell != SpellRegistry.none()) {
            this.remainingRecastCount--;

            AbstractSpell recastSpell = this.currentRecastSpell;

            if (this.remainingRecastCount <= 0) {
                this.currentRecastSpell = SpellRegistry.none();
                this.spellCooldowns.put(recastSpell, this.mob.tickCount + recastSpell.getSpellCooldown());
            }
            return recastSpell;
        }

        ArrayList<AbstractSpell> originalAttack = new ArrayList<>(this.attackSpells);
        ArrayList<AbstractSpell> originalDefense = new ArrayList<>(this.defenseSpells);
        ArrayList<AbstractSpell> originalMovement = new ArrayList<>(this.movementSpells);
        ArrayList<AbstractSpell> originalSupport = new ArrayList<>(this.supportSpells);

        int currentTick = this.mob.tickCount;

        this.attackSpells.removeIf(spell -> spellCooldowns.getOrDefault(spell, 0) > currentTick);
        this.defenseSpells.removeIf(spell -> spellCooldowns.getOrDefault(spell, 0) > currentTick);
        this.movementSpells.removeIf(spell -> spellCooldowns.getOrDefault(spell, 0) > currentTick);
        this.supportSpells.removeIf(spell -> spellCooldowns.getOrDefault(spell, 0) > currentTick);

        AbstractSpell selectedSpell = super.getNextSpellType();

        this.attackSpells.clear(); this.attackSpells.addAll(originalAttack);
        this.defenseSpells.clear(); this.defenseSpells.addAll(originalDefense);
        this.movementSpells.clear(); this.movementSpells.addAll(originalMovement);
        this.supportSpells.clear(); this.supportSpells.addAll(originalSupport);

        if (selectedSpell != SpellRegistry.none()) {
            int spellLevel = (int) (selectedSpell.getMaxLevel() * Mth.lerp(this.mob.getRandom().nextFloat(), this.minSpellQuality, this.maxSpellQuality));
            spellLevel = Math.max(spellLevel, 1);

            int recastCount = selectedSpell.getRecastCount(spellLevel, this.mob);

            if (recastCount >= 2 && !(selectedSpell.getEmptyCastData() instanceof SummonedEntitiesCastData)) {
                this.currentRecastSpell = selectedSpell;
                this.currentRecastLevel = spellLevel;
                this.remainingRecastCount = recastCount - 1;
            } else {
                this.spellCooldowns.put(selectedSpell, currentTick + selectedSpell.getSpellCooldown());
            }
        }

        return selectedSpell;
    }

    protected boolean hasAnyAvailableSpell() {
        if (this.remainingRecastCount > 0 && this.currentRecastSpell != SpellRegistry.none()) {
            return true;
        }

        int currentTick = this.mob.tickCount;

        for (AbstractSpell spell : this.attackSpells) {
            if (spellCooldowns.getOrDefault(spell, 0) <= currentTick) return true;
        }
        for (AbstractSpell spell : this.defenseSpells) {
            if (spellCooldowns.getOrDefault(spell, 0) <= currentTick) return true;
        }
        for (AbstractSpell spell : this.movementSpells) {
            if (spellCooldowns.getOrDefault(spell, 0) <= currentTick) return true;
        }
        for (AbstractSpell spell : this.supportSpells) {
            if (spellCooldowns.getOrDefault(spell, 0) <= currentTick) return true;
        }

        return false;
    }

    @Override
    public boolean canUse() {
        return super.canUse() && hasAnyAvailableSpell();
    }

    @Override
    public boolean canContinueToUse() {
        if (this.isActing()) return super.canContinueToUse();

        return super.canContinueToUse() && hasAnyAvailableSpell();
    }

    @Override
    public void tick() {
        super.tick();

        if (this.spellCastingMob.isCasting() && this.target != null) {
            applyPredictiveAiming(this.target);
        }

        if (this.target != null) {
            if (this.spellCastingMob.isCasting()) {
                this.mob.setSpeed((float) (this.speedModifier * 0.4F));

                if (this.seeTime <= 0) {
                    MagicData mobMagicData = MagicData.getPlayerMagicData(this.mob);
                    if (mobMagicData != null) {
                        mobMagicData.resetCastingState();
                    }
                }
            } else {
                this.mob.setSpeed((float) this.speedModifier);
            }

            if (this.mob.getRandom().nextFloat() < 0.04F) {
                this.strafingClockwise = !this.strafingClockwise;

                if (this.mob.getRandom().nextFloat() < 0.15F) {
                    this.strafeTime = 40;
                }
            }

            if (this.mob.onGround() && this.mob.getRandom().nextFloat() < 0.03F) {
                this.mob.getJumpControl().jump();
            }
        }
    }

    protected void applyPredictiveAiming(LivingEntity targetEntity) {
        Vec3 targetVelocity = targetEntity.getDeltaMovement();

        double distance = Math.sqrt(this.mob.distanceToSqr(targetEntity));

        double leadFactor = distance * 0.35;

        double predictedX = targetEntity.getX() + (targetVelocity.x * leadFactor);
        double predictedZ = targetEntity.getZ() + (targetVelocity.z * leadFactor);

        double predictedY = targetEntity.getY() + targetEntity.getEyeHeight();

        this.mob.getLookControl().setLookAt(predictedX, predictedY, predictedZ, 30.0F, 30.0F);
    }
}
