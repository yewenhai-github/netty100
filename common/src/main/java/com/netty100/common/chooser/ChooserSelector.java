package com.netty100.common.chooser;

import com.netty100.common.utils.SysUtility;

/**
 * @author why
 * @version 1.0.0, 2022/4/18
 * @since 1.0.0, 2022/4/18
 */
public class ChooserSelector {
    private static DefaultEventChannelChooserFactory chooserFactory = DefaultEventChannelChooserFactory.INSTANCE;
    private static EventChannelChooserFactory.EventChannelChooser randomChooser = null;
    private static EventChannelChooserFactory.EventChannelChooser loopChooser = null;

    public static EventChannelChooserFactory.EventChannelChooser getRandomChooser(){
        if(SysUtility.isEmpty(randomChooser)){
            randomChooser = chooserFactory.newChooser(EventChannelChooserFactory.CHOOSER_STRATEGY_RANDOM);
        }
        return randomChooser;
    }

    public static EventChannelChooserFactory.EventChannelChooser getLoopChooser(){
        if(SysUtility.isEmpty(randomChooser)){
            randomChooser = chooserFactory.newChooser(EventChannelChooserFactory.CHOOSER_STRATEGY_LOOP);
        }
        return randomChooser;
    }


}
