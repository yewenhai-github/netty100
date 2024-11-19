package com.netty100.utils;

import com.netty100.pojo.vo.ServerVo;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * @author why
 */
public class RoutingTool {

    public static void process(List<ServerVo> serverVos) {
        class ServerVoComparator implements Comparator<ServerVo> {

            @Override
            public int compare(ServerVo o1, ServerVo o2) {
                Integer connectionCount1 = o1.getConnectionCount();
                Integer connectionCount2 = o2.getConnectionCount();
                if (connectionCount1 > connectionCount2) {
                    return 1;
                } else if (connectionCount1.equals(connectionCount2)) {
                    Integer bootTimes1 = o1.getBootTimes();
                    Integer bootTimes2 = o2.getBootTimes();
                    if (bootTimes1 > bootTimes2) {
                        return 1;
                    } else if (bootTimes1.equals(bootTimes2)) {
                        Date date1 = o1.getLastHeartBeatTime();
                        Date date2 = o2.getLastHeartBeatTime();
                        if (date1.after(date2)) {
                            return -1;
                        } else if (date1.equals(date2)) {
                            return 0;
                        } else {
                            return 1;
                        }
                    } else {
                        return -1;
                    }

                } else {
                    return -1;
                }
            }
        }
        Comparator<ServerVo> comparator = new ServerVoComparator();
        serverVos.sort(comparator);
    }
}
