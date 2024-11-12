//package com.why.test.utils;
//
//import com.why.kernel.devops.utils.WheelTimerUtils;
//import com.why.netty.common.utils.SysUtility;
//import io.netty.util.Timeout;
//import io.netty.util.TimerTask;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.TimeUnit;
///**
// * @author yewenhai
// * @version 1.0.0, 2022/5/27
// * @since 1.0.0, 2022/5/27
// */
//public class WheelTimerTest {
//
//    public static void main(String[] args) throws InterruptedException {
//        final CountDownLatch latch = new CountDownLatch(3);
//
//        WheelTimerUtils.hashedWheel.newTimeout(new TimerTask() {
//            @Override
//            public void run(final Timeout timeout) throws Exception {
//                System.out.println("----------------------------------------"+ SysUtility.getSysDate());
//                latch.countDown();
//            }
//        }, 10, TimeUnit.SECONDS);
//
//        Thread.sleep(1000);
//
//        WheelTimerUtils.hashedWheel.newTimeout(new TimerTask() {
//            @Override
//            public void run(final Timeout timeout) throws Exception {
//                System.out.println("----------------------------------------"+ SysUtility.getSysDate());
//                latch.countDown();
//            }
//        }, 1000, TimeUnit.MILLISECONDS);
//
//        Thread.sleep(1000);
//
//        WheelTimerUtils.hashedWheel.newTimeout(new TimerTask() {
//            @Override
//            public void run(final Timeout timeout) throws Exception {
//                System.out.println("----------------------------------------"+ SysUtility.getSysDate());
//                latch.countDown();
//            }
//        }, 1000, TimeUnit.MILLISECONDS);
//
//
//        latch.await();
//        WheelTimerUtils.hashedWheel.stop();
//    }
//
//
//}
