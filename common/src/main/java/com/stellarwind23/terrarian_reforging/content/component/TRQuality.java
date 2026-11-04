package com.stellarwind23.terrarian_reforging.content.component;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.stellarwind23.terrarian_reforging.TerrarianReforging;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Util;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.Rarity;

import java.util.List;
import java.util.Optional;

public record TRQuality(
        int weight,
        double quality,
        HolderSet<Item> applicableTo,
        Optional<List<AttributeEntry>> modifiers,
        Optional<Rarity> rarity,
        Optional<TextColor> nameColor,
        Optional<Float> handScale,
        Optional<Float> guiScale,
        Optional<Float> durabilityScale,
        Optional<Integer> enchantability) {

    private static final Codec<TRQuality> BODY_CODEC = RecordCodecBuilder.create(q -> q.group(
            ExtraCodecs.POSITIVE_INT.fieldOf("weight").forGetter(TRQuality::weight),
            Codec.DOUBLE.fieldOf("quality").forGetter(TRQuality::quality),
            RegistryCodecs.homogeneousList(Registries.ITEM).fieldOf("valid_items").forGetter(TRQuality::applicableTo),
            AttributeEntry.LIST_CODEC.optionalFieldOf("modifiers").forGetter(TRQuality::modifiers),
            Rarity.CODEC.optionalFieldOf("rarity").forGetter(TRQuality::rarity),
            TextColor.CODEC.optionalFieldOf("name_color").forGetter(TRQuality::nameColor),
            ExtraCodecs.floatRange(0.1F, 2F).optionalFieldOf("hand_scale").forGetter(TRQuality::handScale),
            ExtraCodecs.floatRange(0.1F, 2F).optionalFieldOf("gui_scale").forGetter(TRQuality::guiScale),
            ExtraCodecs.floatRange(0.01F, 10F).optionalFieldOf("durability_scale").forGetter(TRQuality::durabilityScale),
            ExtraCodecs.intRange(-30, 30).optionalFieldOf("enchantability").forGetter(TRQuality::enchantability)
    ).apply(q, TRQuality::new));

    /** Stand-in for a file whose required mods are missing: valid for no item, so it can never roll. */
    private static final TRQuality DISABLED = new TRQuality(1, 0.0, HolderSet.empty(),
            Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
            Optional.empty(), Optional.empty(), Optional.empty());

    /** File-level "required_mods" is checked before anything else is decoded. */
    public static final Codec<TRQuality> DIRECT_CODEC = new Codec<>() {
        @Override
        public <T> DataResult<Pair<TRQuality, T>> decode(DynamicOps<T> ops, T input) {
            return TRMods.satisfied(ops, input).flatMap(ok -> {
                if (ok) return BODY_CODEC.decode(ops, input);
                return DataResult.success(Pair.of(DISABLED, input));
            });
        }

        @Override
        public <T> DataResult<T> encode(TRQuality quality, DynamicOps<T> ops, T prefix) {
            return BODY_CODEC.encode(quality, ops, prefix);
        }
    };

    //Reference codec: serializes an id referencing registered quality components
    public static final Codec<Holder<TRQuality>> HOLDER_CODEC = RegistryFixedCodec.create(TerrarianReforging.REG_KEY);

    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<TRQuality>> STREAM_CODEC =
            ByteBufCodecs.holderRegistry(TerrarianReforging.REG_KEY);

    public record AttributeEntry(
            Holder<Attribute> attribute,
            double amount,
            AttributeModifier.Operation operation,
            EquipmentSlotGroup slots
    ) {
        public static final Codec<AttributeEntry> CODEC = RecordCodecBuilder.create(ae -> ae.group(
                Attribute.CODEC.fieldOf("attribute").forGetter(AttributeEntry::attribute),
                Codec.DOUBLE.fieldOf("amount").forGetter(AttributeEntry::amount),
                AttributeModifier.Operation.CODEC.fieldOf("operation").forGetter(AttributeEntry::operation),
                EquipmentSlotGroup.CODEC.optionalFieldOf("slot", EquipmentSlotGroup.ANY).forGetter(AttributeEntry::slots)
        ).apply(ae, AttributeEntry::new));

        /** Per-entry "required_mods": decodes to empty, without touching the attribute, if a mod is missing. */
        public static final Codec<Optional<AttributeEntry>> CONDITIONAL = new Codec<>() {
            @Override
            public <T> DataResult<Pair<Optional<AttributeEntry>, T>> decode(DynamicOps<T> ops, T input) {
                return TRMods.satisfied(ops, input).flatMap(ok -> {
                    if (!ok) return DataResult.success(Pair.of(Optional.<AttributeEntry>empty(), input));
                    return CODEC.decode(ops, input).map(p -> p.mapFirst(Optional::of));
                });
            }

            @Override
            public <T> DataResult<T> encode(Optional<AttributeEntry> entry, DynamicOps<T> ops, T prefix) {
                return entry.isPresent() ? CODEC.encode(entry.get(), ops, prefix) : DataResult.success(prefix);
            }
        };

        public static final Codec<List<AttributeEntry>> LIST_CODEC = CONDITIONAL.listOf().xmap(
                l -> l.stream().flatMap(Optional::stream).toList(),
                l -> l.stream().map(Optional::of).toList());

        //get as unique modifier(doesn't stack with itself)
        public AttributeModifier toModifier(Identifier qualityId) {
            Identifier attrId = attribute.unwrapKey().map(ResourceKey::identifier).orElseThrow();
            Identifier modId = Identifier.fromNamespaceAndPath(
                    qualityId.getNamespace(),
                    TerrarianReforging.CMP_NAME + "/" + qualityId.getPath() + "/" + attrId.getNamespace() + "_" + attrId.getPath()
            );
            return new AttributeModifier(modId, amount, operation);
        }
    }

    //tr_quality.namespace.squishy -> "Squishy %s" (translatable prefix per lang)
    public static String translationKey(Identifier id) {
        return Util.makeDescriptionId(TerrarianReforging.CMP_NAME, id);
    }

    public Component decorate(Identifier id, Component baseName) {
        return Component.translatable(translationKey(id), baseName);
    }

    public float scaleFor(ItemDisplayContext ctx) {
        return switch (ctx) {
            case GUI -> guiScale.orElse(1F);
            case FIRST_PERSON_LEFT_HAND, FIRST_PERSON_RIGHT_HAND,
                 THIRD_PERSON_LEFT_HAND, THIRD_PERSON_RIGHT_HAND,
                 GROUND -> handScale.orElse(1F);
            default -> 1F;
        };
    }

    public Style styleOr(Style fallback) {
        return nameColor.map(fallback::withColor).orElse(fallback);
    }
}