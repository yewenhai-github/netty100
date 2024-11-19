package com.netty100.mapper;

import com.netty100.entity.ClientConnectionHistory;
import com.netty100.pojo.dto.ClientHistoryConnectDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author why
 * @since 2022-03-25
 */
public interface ClientConnectionHistoryMapper {

    int deleteConnectionLogs(@Param(value = "table") String table, @Param(value = "ids") List<Integer> ids);

    List<Integer> selectClearIds(@Param(value = "table") String table, @Param(value = "delEndTime") String delEndTime, @Param(value = "pageSize") Integer pageSize);

    void batchSave(@Param("tableName") String tableName, @Param("list") List<ClientConnectionHistory> list);

    ClientConnectionHistory getLatestDisconnectRecord(@Param("tableName") String tableName, @Param("channelId") String channelId);

    List<ClientHistoryConnectDto> getRangeTimeHistoryConnect(@Param(value = "table") String table, @Param(value = "occurTimeBegin") String occurTimeBegin, @Param(value = "occurTimeEnd") String occurTimeEnd);
}
