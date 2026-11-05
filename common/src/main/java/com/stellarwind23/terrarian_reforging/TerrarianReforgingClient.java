package com.stellarwind23.terrarian_reforging;

import com.stellarwind23.terrarian_reforging.content.component.TRDataComponentTypes;
import com.stellarwind23.terrarian_reforging.content.component.TRQualityComponent;
import com.stellarwind23.terrarian_reforging.content.component.TRSharpenedComponent;
import dev.architectury.event.events.client.ClientTooltipEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class TerrarianReforgingClient {

    public static void init() {
        ClientTooltipEvent.ITEM.register(((stack, lines, tooltipContext, flag) -> {
            TRQualityComponent qc = stack.get(TRDataComponentTypes.TR_QUALITY_COMPONENT.get());
            TRSharpenedComponent sc = stack.get(TRDataComponentTypes.TR_SHARPENED_COMPONENT.get());

            List<Component> extra = new ArrayList<>();

            if (qc != null && qc.quality().isBound()) {
                int added = qc.printLines(lines::add);
                if (added > 0 && stack.isEnchanted()) lines.add(CommonComponents.EMPTY);
            }

            if(sc != null) {
                sc.addToTooltip(tooltipContext, extra::add, flag);
            }

            //Insert right under the item name
            lines.addAll(Math.min(1, lines.size()), extra);
        }));
    }
}
