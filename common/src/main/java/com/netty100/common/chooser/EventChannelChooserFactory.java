package com.netty100.common.chooser;

import java.util.List;

public interface EventChannelChooserFactory {
    public static final int CHOOSER_STRATEGY_RANDOM = 1;
    public static final int CHOOSER_STRATEGY_LOOP = 2;

    EventChannelChooser newChooser(int strategy);

    interface EventChannelChooser {
        Object next(List list);
    }
}
