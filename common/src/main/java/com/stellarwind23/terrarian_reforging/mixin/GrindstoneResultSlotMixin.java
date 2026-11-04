package com.stellarwind23.terrarian_reforging.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.stellarwind23.terrarian_reforging.content.TRItems;
import com.stellarwind23.terrarian_reforging.content.component.TRDataComponentTypes;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(targets = "net.minecraft.world.inventory.GrindstoneMenu$4")
public class GrindstoneResultSlotMixin {

    @WrapMethod(method = "onTake")
    private void tr$keepCopperWool(Player arg, ItemStack arg2, Operation<Void> original) {
        if (!(arg.containerMenu instanceof GrindstoneMenu menu)) {
            original.call(arg, arg2);
            return;
        }

        Container in = ((GrindstoneMenuAccessor) menu).tr$repairSlots();
        Item wool = TRItems.COPPER_WOOL.get();
        ItemStack leftover = ItemStack.EMPTY;
        int slotIdx = -1;

        for (int i = 0; i < 2; i++) {
            ItemStack s = in.getItem(i), other = in.getItem(1 - i);
            if (s.is(wool) && !other.is(wool)
                    && other.has(TRDataComponentTypes.TR_QUALITY_COMPONENT.get())) {
                int dmg = s.getDamageValue() + 1;
                if (dmg < s.getMaxDamage()) {          // otherwise it's worn out and stays gone
                    leftover = s.copy();
                    leftover.setDamageValue(dmg);
                    slotIdx = i;
                }
                break;
            }
        }

        original.call(arg, arg2);                  // vanilla gives XP and clears both slots
        if (!leftover.isEmpty()) in.setItem(slotIdx, leftover);
    }
}