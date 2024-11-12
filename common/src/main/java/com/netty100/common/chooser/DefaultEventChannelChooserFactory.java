package com.netty100.common.chooser;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public final class DefaultEventChannelChooserFactory implements EventChannelChooserFactory {

    public static final DefaultEventChannelChooserFactory INSTANCE = new DefaultEventChannelChooserFactory();

    private DefaultEventChannelChooserFactory() { }

    @Override
    public EventChannelChooser newChooser(int strategy) {
        if(strategy == EventChannelChooserFactory.CHOOSER_STRATEGY_RANDOM){
            return new RandomEventExecutorChooser();
        }else if(strategy == EventChannelChooserFactory.CHOOSER_STRATEGY_LOOP){
            return new GenericEventExecutorChooser();
        }else{
            //2的n次方算法
            return new PowerOfTwoEventExecutorChooser();
        }
    }

    //2的n次方时，返回true
    private static boolean isPowerOfTwo(int val) {
        return (val & -val) == val;
    }

    private static final class RandomEventExecutorChooser implements EventChannelChooser {

        @Override
        public Object next(List list) {
            return list.get(ThreadLocalRandom.current().nextInt(0, list.size()));
        }
    }

    private static final class PowerOfTwoEventExecutorChooser implements EventChannelChooser {
        private final AtomicInteger idx = new AtomicInteger();

        @Override
        public Object next(List list) {
            return list.get(idx.getAndIncrement() & list.size() - 1);
        }
    }

    private static final class GenericEventExecutorChooser implements EventChannelChooser {
        // Use a 'long' counter to avoid non-round-robin behaviour at the 32-bit overflow boundary.
        // The 64-bit long solves this by placing the overflow so far into the future, that no system
        // will encounter this in practice.
        private final AtomicLong idx = new AtomicLong();

        @Override
        public Object next(List list) {
            int chooser = (int) Math.abs(idx.getAndIncrement() % list.size());
            return list.get(chooser);
        }
    }
}
