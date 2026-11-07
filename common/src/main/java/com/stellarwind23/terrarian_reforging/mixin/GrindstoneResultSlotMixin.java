package com.stellarwind23.terrarian_reforging.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.stellarwind23.terrarian_reforging.util.TRGrindstone;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net.minecraft.world.inventory.GrindstoneMenu$4")
public class GrindstoneResultSlotMixin {

    @WrapMethod(method = "onTake")
    private void tr$keepTool(Player player, ItemStack carried, Operation<Void> original) {
        if (player.containerMenu instanceof GrindstoneMenu menu) {
            Container in = ((GrindstoneMenuAccessor) menu).tr$repairSlots();
            ItemStack a = in.getItem(0), b = in.getItem(1);
            if (TRGrindstone.kind(a, b) != TRGrindstone.Kind.NONE) {
                int idx = TRGrindstone.toolSlot(a, b);
                ItemStack leftover = in.getItem(idx).copy();
                int dmg = leftover.getDamageValue() + 1;
                boolean worn = dmg >= leftover.getMaxDamage();
                leftover.setDamageValue(dmg);

                TRGrindstone.NO_XP.set(true);
                try { original.call(player, carried); } finally { TRGrindstone.NO_XP.remove(); }
                if (!worn) in.setItem(idx, leftover);
                return;
            }
        }
        original.call(player, carried);
    }

    @ModifyReturnValue(method = "getExperienceFromItem", at = @At("RETURN"))
    private int tr$noXp(int xp) {
        return TRGrindstone.NO_XP.get() ? 0 : xp;
    }
}