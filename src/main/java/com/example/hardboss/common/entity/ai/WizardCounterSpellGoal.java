package com.example.hardboss.common.entity.ai;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class WizardCounterSpellGoal extends Goal {
    protected final AbstractSpellCastingMob mob;
    protected final AbstractSpell counterSpell;

    protected LivingEntity target;

    private int nextAvailableTick = 0;
    private int delayTick = 6;

    public WizardCounterSpellGoal(AbstractSpellCastingMob mob) {
        this.mob = mob;
        this.counterSpell = SpellRegistry.COUNTERSPELL_SPELL.get();

        this.setFlags(EnumSet.of(Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        this.target = this.mob.getTarget();
        if (this.target == null || !this.target.isAlive()) return false;
        if (this.mob.isCasting()) return false;

        if (this.mob.tickCount < this.nextAvailableTick) return false;

        MagicData magicData = MagicData.getPlayerMagicData(this.target);
        if (magicData == null) return false;

        CastType castType = magicData.getCastType();
        return magicData.isCasting() && (castType != CastType.INSTANT && castType != CastType.NONE);
    }

    @Override
    public void tick() {
        if (this.target == null) return;

        this.delayTick--;
        if (this.delayTick == 0) {
            this.mob.getLookControl().setLookAt(target.getX(), this.target.getEyeY(), target.getY(), 15.0f, 15.0f);

            this.mob.initiateCastSpell(this.counterSpell, 1);
            this.nextAvailableTick = this.mob.tickCount + Utils.applyCooldownReduction(this.counterSpell.getSpellCooldown(), this.mob);
        }
    }

    @Override
    public boolean canContinueToUse() {
        return (this.mob.isCasting() || this.delayTick > 0) && this.target != null && this.target.isAlive();
    }

    @Override
    public void stop() {
        this.delayTick = 6;
        this.target = null;
    }
}
