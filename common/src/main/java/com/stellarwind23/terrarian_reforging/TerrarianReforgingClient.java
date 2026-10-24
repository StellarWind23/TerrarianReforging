package com.stellarwind23.terrarian_reforging;

import com.stellarwind23.terrarian_reforging.content.component.TRDataComponentTypes;
import com.stellarwind23.terrarian_reforging.content.component.TRQualityComponent;
import dev.architectury.event.events.client.ClientTooltipEvent;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class TerrarianReforgingClient {

    public static void init() {
        ClientTooltipEvent.ITEM.register(((stack, lines, tooltipContext, flag) -> {
            TRQualityComponent qc = stack.get(TRDataComponentTypes.TR_QUALITY_COMPONENT.get());
            if (qc == null || !qc.quality().isBound()) return;

            //Insert right under the item name
            List<Component> extra = new ArrayList<>();
            qc.addToTooltip(tooltipContext, extra::add,flag, stack);
            lines.addAll(Math.min(1, lines.size()), extra);
        }));
    }
}
