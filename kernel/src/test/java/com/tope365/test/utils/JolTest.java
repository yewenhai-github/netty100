//package com.why.test.utils;
//
//import org.openjdk.jol.info.ClassLayout;
//import org.openjdk.jol.info.GraphLayout;
//
//import java.util.HashSet;
//import java.util.Set;
//
///**
// * @author yewenhai
// * @version 1.0.0, 2022/6/22
// * @since 1.0.0, 2022/6/22
// */
//public class JolTest {
//
//    public static void main(String[] args) {
////        System.setProperty("-XX:-UseCompressedOops", "true");
//        Set<String> test = new HashSet<>();
//        test.add("aaa");
////        相关方法：
////        1.使用jol计算对象的大小（单位为字节）：
//        System.out.println(ClassLayout.parseInstance(test).instanceSize());
////        2.使用jol查看对象内部的内存布局：
//        System.out.println(ClassLayout.parseInstance(test).toPrintable());
////        3.查看对象外部信息：包括引用的对象：
//        System.out.println(GraphLayout.parseInstance(test).toPrintable());
////        4.查看对象占用空间总大小（单位为字节，1024Byte = 1kb）：
//        System.out.println(GraphLayout.parseInstance(test).totalSize());
//    }
//
//
//
//}
