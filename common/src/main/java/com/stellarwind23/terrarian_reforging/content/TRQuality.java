package com.stellarwind23.terrarian_reforging.content;

import com.mojang.serialization.Codec;
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

import java.util.List;
import java.util.Optional;

public record TRQuality(
        int weight,
        HolderSet<Item> applicableTo,
        List<AttributeEntry> modifiers,
        Optional<TextColor> color,
        Optional<Float> hand_scale,
        Optional<Float> gui_scale)
{

    public static final Codec<TRQuality> DIRECT_CODEC = RecordCodecBuilder.create(q -> q.group(
            ExtraCodecs.POSITIVE_INT.fieldOf("weight").forGetter(TRQuality::weight),
            RegistryCodecs.homogeneousList(Registries.ITEM).fieldOf("valid_items").forGetter(TRQuality::applicableTo),
            AttributeEntry.CODEC.listOf().fieldOf("modifiers").forGetter(TRQuality::modifiers),
            TextColor.CODEC.optionalFieldOf("color").forGetter(TRQuality::color),
            ExtraCodecs.floatRange(0F, 1F).optionalFieldOf("hand_scale").forGetter(TRQuality::hand_scale),
            ExtraCodecs.floatRange(0F, 1F).optionalFieldOf("gui_scale").forGetter(TRQuality::gui_scale)
            ).apply(q, TRQuality::new));

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

    public Style styleOr(Style fallback) {
        return color.map(fallback::withColor).orElse(fallback);
    }

    public float scaleFor(ItemDisplayContext ctx) {
        return switch (ctx) {
            case GUI -> gui_scale.orElse(1F);
            case FIRST_PERSON_LEFT_HAND, FIRST_PERSON_RIGHT_HAND,
                 THIRD_PERSON_LEFT_HAND, THIRD_PERSON_RIGHT_HAND,
                 GROUND -> hand_scale.orElse(1F);
            default -> 1F;
        };
    }
}
