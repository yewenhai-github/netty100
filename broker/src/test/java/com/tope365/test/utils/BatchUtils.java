package com.netty100.test.utils;

import java.util.ArrayList;
import java.util.List;

public class BatchUtils {

    private static final int batchNum = 5000;

    public static void main(String[] args) {
        List<Object> entityList = new ArrayList<Object>();
        for (int i = 0; i < 6300; i++) {
            if(i < 5000){
                entityList.add("111");
            }else{
                entityList.add("222");
            }
        }

        List<Object> tempList = new ArrayList<Object>();
        for (int i = 0; i <= entityList.size()/batchNum; i++) {
            int fromIndex = i*batchNum;
            int toIndex = (i+1)*batchNum > entityList.size() ? fromIndex + entityList.size()%batchNum : (i+1)*batchNum;
            tempList.addAll(entityList.subList(fromIndex, toIndex));
            System.out.println(tempList.size() + "  " + tempList.get(0));
            tempList.clear();
        }

    }

}
