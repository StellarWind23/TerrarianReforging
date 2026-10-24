package com.stellarwind23.terrarian_reforging.content.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.stellarwind23.terrarian_reforging.TerrarianReforging;
import com.stellarwind23.terrarian_reforging.content.component.TRDataComponentTypes;
import com.stellarwind23.terrarian_reforging.content.component.TRQuality;
import com.stellarwind23.terrarian_reforging.util.TRQualityRoller;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;

public final class TRQualityCommand {
    private TRQualityCommand() {}

    private static final DynamicCommandExceptionType NO_ITEM = new DynamicCommandExceptionType(
            name -> Component.literal(name + " is not holding an item"));

    private static final Dynamic2CommandExceptionType UNSUPPORTED = new Dynamic2CommandExceptionType(
            (item, quality) -> Component.literal(item + " can't have the quality " + quality));

    private static final SimpleCommandExceptionType NOTHING_HAPPENED =
            new SimpleCommandExceptionType(Component.literal("Nothing happened"));

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext ctx) {
        dispatcher.register(Commands.literal("reforge")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.argument("targets", EntityArgument.entities())
                        .then(Commands.literal("clear")
                                .executes(c -> clear(c.getSource(), EntityArgument.getEntities(c, "targets"))))
                        .then(Commands.argument("quality", ResourceArgument.resource(ctx, TerrarianReforging.REG_KEY))
                                .executes(c -> set(c.getSource(),
                                        EntityArgument.getEntities(c, "targets"),
                                        ResourceArgument.getResource(c, "quality", TerrarianReforging.REG_KEY))))));
    }

    private static int set(CommandSourceStack src, Collection<? extends Entity> targets,
                           Holder<TRQuality> quality) throws CommandSyntaxException {
        String id = quality.unwrapKey().map(k -> k.identifier().toString()).orElse("?");
        int count = 0;
        LivingEntity single = null;
        ItemStack singleStack = ItemStack.EMPTY;

        for (Entity e : targets) {
            if (!(e instanceof LivingEntity living)) continue;
            ItemStack stack = living.getMainHandItem();
            if (targets.size() == 1) { single = living; singleStack = stack; }

            if (stack.isEmpty() || !TRQualityRoller.supports(quality, stack)) continue;

            TRQualityRoller.apply(stack, quality);
            count++;
        }

        if (count == 0) {
            if (targets.size() == 1 && single != null) {
                if (singleStack.isEmpty()) throw NO_ITEM.create(single.getName().getString());
                throw UNSUPPORTED.create(singleStack.getHoverName().getString(), id);
            }
            throw NOTHING_HAPPENED.create();
        }

        int n = count;
        src.sendSuccess(() -> Component.literal("Applied " + id + " to " + n + " item(s)"), true);
        return count;
    }

    private static int clear(CommandSourceStack src, Collection<? extends Entity> targets) {
        int count = 0;
        for (Entity e : targets) {
            if (!(e instanceof LivingEntity living)) continue;
            ItemStack stack = living.getMainHandItem();
            if (stack.isEmpty() || !stack.has(TRDataComponentTypes.TR_QUALITY_COMPONENT.get())) continue;
            TRQualityRoller.clear(stack);
            count++;
        }
        int n = count;
        src.sendSuccess(() -> Component.literal("Cleared quality from " + n + " item(s)"), true);
        return count;
    }
}