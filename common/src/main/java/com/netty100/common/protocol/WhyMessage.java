package com.netty100.common.protocol;

import com.netty100.common.constants.CommonConstants;
import com.netty100.common.utils.SysUtility;
import lombok.Data;

import java.io.*;
import java.nio.ByteBuffer;

/**
 * @author yewenhai
 * @version 1.0.0, 2022/3/23
 * @since 1.0.0, 2022/3/23
 */
@Data
public class WhyMessage implements Serializable {
    WhyMessageFixedHeader fixedHeader = new WhyMessageFixedHeader();
    WhyMessageVariableHeader variableHeader = new WhyMessageVariableHeader();
    byte[] payload;

    public byte[] bytes(){
        return buffer().array();
    }

    public ByteBuffer buffer(){
        return WhyMessageFactory.encode(this);
    }

    public Integer length(){
        int len = CommonConstants.MESSAGE_FIXED_HEADER_LENGTH + (SysUtility.isNotEmpty(payload) ? payload.length : 0);
        if (this.fixedHeader.variableFlag && this.variableHeader.apiVersion == CommonConstants.DEFAULT_VARIABLE_API_VERSION) {
            len += CommonConstants.MESSAGE_VARIABLE_HEADER_LENGTH_V0;
        }
        return len;
    }

    // 实现深拷贝
    public WhyMessage deepClone() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(this);

            ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bis);
            return (WhyMessage) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("深拷贝失败：" + e);
        }
    }

}
