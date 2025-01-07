package com.netty100.common.protocol;

import com.netty100.common.constants.CommonConstants;
import com.netty100.common.utils.SysUtility;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;

@Slf4j
public final class WhyMessageFactory {

    public static WhyMessage newMessage(WhyMessage whyMsg, byte[] body) {
        WhyMessage msg = newMessage(whyMsg.getFixedHeader(), whyMsg.getVariableHeader(), body);
        msg.getFixedHeader().setMessageSerialize(WhyMessageCode.serialize_string.getCode());
        return msg;
    }

    public static WhyMessage newDefaultMessage(byte messageWay, Long userId, byte[] body, boolean variableFlag) {
        byte messageSource = 0;
        byte messageDest = 0;
        byte messageType = 1;
        byte messageSerialize = 1;
        Long deviceId = -1L;
        Long messageId = WhyMessageFactory.createMessageId();

        WhyMessageFixedHeader fixedHeader = WhyMessageFactory.newFixedHeader(messageWay, messageSource, messageDest, messageType, messageSerialize, messageId, deviceId, userId, variableFlag);
        WhyMessageVariableHeader variableHeader = WhyMessageFactory.defaultVariableHeader();
        return WhyMessageFactory.newMessage(fixedHeader, variableHeader, body);
    }

    public static WhyMessage newDefaultMessage(byte messageWay, byte messageSource, byte messageDest, Long userId, byte[] body, boolean variableFlag) {
        byte messageType = 1;
        byte messageSerialize = 1;
        Long deviceId = -1L;

        WhyMessageFixedHeader fixedHeader = newFixedHeader(messageWay, messageSource, messageDest, messageType, messageSerialize, createMessageId(), deviceId, userId, variableFlag);
        return newMessage(fixedHeader, defaultVariableHeader(), body);
    }



    public static WhyMessageFixedHeader newFixedHeader(byte messageWay, byte messageSource, byte messageDest, byte messageType, byte messageSerialize, Long messageId, Long deviceId, Long userId, boolean variableFlag){
        WhyMessageFixedHeader fixedHeader = new WhyMessageFixedHeader();
        fixedHeader.setMessageWay(messageWay);
        fixedHeader.setMessageSource(messageSource);
        fixedHeader.setMessageDest(messageDest);
        fixedHeader.setMessageType(messageType);
        fixedHeader.setMessageSerialize(messageSerialize);
        fixedHeader.setMessageId(messageId);
        fixedHeader.setDeviceId(deviceId);
        fixedHeader.setUserId(userId);
        fixedHeader.setVariableFlag(variableFlag);
        return fixedHeader;
    }

    public static WhyMessageVariableHeader newVariableHeader(byte apiVersion, boolean isLeaf, boolean isTwoWayMsg, boolean isFirstTime, boolean isDup, WhyMessageQoS qosLevel, boolean isRewrite, int expiresTime){
        WhyMessageVariableHeader variableHeader = new WhyMessageVariableHeader();
        variableHeader.setVariableLength(CommonConstants.MESSAGE_VARIABLE_HEADER_LENGTH_V0);
        variableHeader.setApiVersion(apiVersion);
        variableHeader.isLeaf = isTwoWayMsg;
        variableHeader.isTwoWayMsg = isTwoWayMsg;
        variableHeader.isFirstTime = isFirstTime;
        variableHeader.isDup = isDup;
        variableHeader.setQosLevel(qosLevel);
        variableHeader.isRewrite = isRewrite;
        variableHeader.setExpiresTime(expiresTime);
        return variableHeader;
    }

    public static WhyMessage newMessage(WhyMessageFixedHeader fixedHeader, WhyMessageVariableHeader variableHeader, byte[] body){
        WhyMessage msg = new WhyMessage();
        msg.setFixedHeader(fixedHeader);
        msg.setVariableHeader(SysUtility.isNotEmpty(variableHeader) ? variableHeader : msg.getVariableHeader());
        msg.setPayload(body);
        return msg;
    }

    public static WhyMessageVariableHeader defaultVariableHeader(){
        return newVariableHeader(CommonConstants.DEFAULT_VARIABLE_API_VERSION, CommonConstants.DEFAULT_IS_TWO_WAY_MSG,
                CommonConstants.DEFAULT_IS_FIRST_TIME, false, false, WhyMessageQoS.AT_MOST_ONCE, CommonConstants.DEFAULT_VARIABLE_IS_REWRITE, CommonConstants.DEFAULT_EXPIRES_TIME);
    }

    public static Long createMessageId(){
        return System.currentTimeMillis() * 1000000L + System.nanoTime() % 1000000L;
    }


    /**
     * 对应的16进制如下：（部分）
     * 00000001 对应 0x01
     * 00000010 对应 0x02
     * 00000100 对应 0x04
     * 00001000 对应 0x08
     * 00010000 对应 0x10
     * 00100000 对应 0x20
     * 01000000 对应 0x40
     * 10000000 对应 0x80
     * **/
    public static WhyMessage decode(ByteBuf buffer, int dataLength){
        short f1 = buffer.readUnsignedByte();
        //取出最高位的值，0x80 = 1000 0000
        boolean variableFlag = (f1 & 0x80) == 0x80;
        //读取低7位的值，0x7F = 0111 1111
        byte messageWay = (byte) (f1 & 0x7F);
        //消息对象
        WhyMessage whyMsg = new WhyMessage();
        //读取固定头
        decodeFixedHeader(buffer, whyMsg, variableFlag, messageWay);
        //报文范围校验
        if(messageWay > 25){
            printDecodeError(whyMsg, messageWay);
            return whyMsg;
        }
        //读取可变头
        if(variableFlag){
            decodeVariableHeader(buffer, whyMsg);
        }
        //读取消息载体
        decodePayload(buffer, dataLength, whyMsg);

        return whyMsg;
    }

    private static void decodePayload(ByteBuf buffer, int dataLength, WhyMessage whyMsg) {
        int payloadLength = dataLength - CommonConstants.MESSAGE_FIXED_HEADER_LENGTH - whyMsg.variableHeader.getVariableLength();
        whyMsg.setPayload(new byte[payloadLength]);
        if(payloadLength > 0){
            buffer.readBytes(whyMsg.getPayload());
        }
    }

    private static void decodeVariableHeader(ByteBuf buffer, WhyMessage whyMsg) {
        whyMsg.variableHeader.setVariableLength((short) buffer.readUnsignedShort());
        whyMsg.variableHeader.setApiVersion((byte) buffer.readUnsignedByte());
        short v4 = buffer.readUnsignedByte();

        //leaf字段占据v4的第7位（从右往左数），因此可以通过与0x40（01000000）进行按位与操作来判断它是否为1。
        boolean leaf = (v4 & 0x40) == 0x40;
        //twoWayMsg字段占据v4的第6位（从右往左数），因此可以通过与0x20（00100000）进行按位与操作来判断它是否为1。
        boolean twoWayMsg = (v4 & 0x20) == 0x20;
        //firstTime字段占据v4的第5位（从右往左数），因此可以通过与0x10（00010000）进行按位与操作来判断它是否为1。
        boolean firstTime = (v4 & 0x10) == 0x10;
        //dupFlag字段占据v4的第4位（从右往左数），因此可以通过与0x08（00001000）进行按位与操作来判断它是否为1。
        boolean dupFlag = (v4 & 0x08) == 0x08;
        //qosLevel字段占据v4的第3和第2位，因此可以通过与0x06（00000110）进行按位与操作后再右移1位来得到它的值。 按位与第3和第2位会被保留下来，其余位都被置为0。
        int qosLevel = (v4 & 0x06) >> 1;
        //rewriteFlag字段占据v4的最低位，因此可以通过与0x01（00000001）进行按位与操作来判断它是否为1。
        boolean rewriteFlag = (v4 & 0x01) != 0;

        whyMsg.variableHeader.setLeaf(leaf);
        whyMsg.variableHeader.setTwoWayMsg(twoWayMsg);
        whyMsg.variableHeader.setFirstTime(firstTime);
        whyMsg.variableHeader.setDup(dupFlag);
        whyMsg.variableHeader.setQosLevel(WhyMessageQoS.valueOf(qosLevel));
        whyMsg.variableHeader.setRewrite(rewriteFlag);
        whyMsg.variableHeader.setExpiresTime((int) buffer.readUnsignedInt());
    }

    private static void decodeFixedHeader(ByteBuf buffer, WhyMessage whyMsg, boolean variableFlag, byte messageWay) {
        whyMsg.fixedHeader.setVariableFlag(variableFlag);
        whyMsg.fixedHeader.setMessageWay(messageWay);
        whyMsg.fixedHeader.setMessageSource((byte) buffer.readUnsignedByte());
        whyMsg.fixedHeader.setMessageDest((byte) buffer.readUnsignedByte());
        whyMsg.fixedHeader.setMessageType((byte) buffer.readUnsignedByte());
        whyMsg.fixedHeader.setMessageSerialize((byte) buffer.readUnsignedByte());
        whyMsg.fixedHeader.setMessageId(buffer.readLong());
        whyMsg.fixedHeader.setDeviceId(buffer.readLong());
        whyMsg.fixedHeader.setUserId(buffer.readLong());
    }

    private static void printDecodeError(WhyMessage whyMsg, byte messageWay) {
        log.error("未知报文：way={},source={},dest={},type={},serialize={},id={},deviceId={},userId={},",
                messageWay,
                whyMsg.fixedHeader.getMessageSource(),
                whyMsg.fixedHeader.getMessageDest(),
                whyMsg.fixedHeader.getMessageType(),
                whyMsg.fixedHeader.getMessageSerialize(),
                whyMsg.fixedHeader.getMessageId(),
                whyMsg.fixedHeader.getDeviceId(),
                whyMsg.fixedHeader.getUserId());
    }

    public static ByteBuffer encode(WhyMessage whyMessage){
        ByteBuffer buff = ByteBuffer.allocate(whyMessage.length());

        short unsignedFixedByte1 = 0;
        //[1]字节 Bit[7] 最高位
        if(whyMessage.fixedHeader.variableFlag){
            unsignedFixedByte1 |= 0x80;
        }
        //位或运算符（|），如果两个对应位中有一个为1，则结果为1。
        unsignedFixedByte1 |= whyMessage.fixedHeader.getMessageWay();
        buff.put((byte) unsignedFixedByte1);
        buff.put(whyMessage.fixedHeader.getMessageSource());
        buff.put(whyMessage.fixedHeader.getMessageDest());
        buff.put(whyMessage.fixedHeader.getMessageType());
        buff.put(whyMessage.fixedHeader.getMessageSerialize());
        buff.putLong(whyMessage.fixedHeader.getMessageId());
        buff.putLong(whyMessage.fixedHeader.getDeviceId());
        buff.putLong(whyMessage.fixedHeader.getUserId());

        if(whyMessage.fixedHeader.variableFlag){
            // [1-2]字节 可变头的长度（可变头的字段，可以动态添加与删除，内核逻辑支持对应的位解析）
            buff.putShort(whyMessage.variableHeader.getVariableLength());
            // [3]字节 可变头的版本号，可以支持多个版本
            buff.put(whyMessage.variableHeader.getApiVersion());
            byte vByte4 = 0;
            // [4]字节 Bit[7]为保留字段，都是0，所以不用处理

            //[4]字节 Bit[6]日志的节点，0主节点 1叶子节点，[只有sdk触发的消息才需要区分叶子，属于第一次消息]
            if(whyMessage.variableHeader.isLeaf) {
                vByte4 |= 0x40;
            }
            //[4]字节 Bit[5]如果该值为1，表示发送客户端的同时发送一份到服务端，0表示只发送客户端
            if(whyMessage.variableHeader.isTwoWayMsg) {
                vByte4 |= 0x20;
            }
            // [4]字节 Bit[4]如果该值为1，表示如果用户在线则直接发送，0表示下一次用户登录时发送
            if(whyMessage.variableHeader.isFirstTime) {
                vByte4 |= 0x10;
            }
            // [4]字节 Bit[3]如果该值为1，表明这个数据包是一条重复的消息；否则该数据包就是第一次发布的消息。
            if(whyMessage.variableHeader.isDup) {
                vByte4 |= 0x08;
            }
            // [4]字节 Bit[2-1]为Qos字段：Bit1和Bit2为0表示QoS 0：至多一次；Bit1为1表示QoS1：至少一次；Bit2 为1表示QoS 2：只有一次；
            switch(whyMessage.variableHeader.getQosLevel()) {
                case AT_MOST_ONCE:
                    break;
                case AT_LEAST_ONCE:
                    vByte4 |= 0x02;
                    break;
                case EXACTLY_ONCE:
                    vByte4 |= 0x04;
                    break;
                default:
                    break;
            }
            // [4]字节 Bit[0] 是否剔除可变头
            if(whyMessage.variableHeader.isRewrite()) {
                vByte4 |= 0x01;
            }
            buff.put(vByte4);
            //[5-8]字节 离线消息有效时间，单位默认（秒）
            buff.putInt(whyMessage.variableHeader.getExpiresTime());
        }


        if(SysUtility.isNotEmpty(whyMessage.getPayload())){
            buff.put(whyMessage.getPayload());
        }
        return buff;
    }

    public static ByteBuf buffer2buf(ByteBuffer buff){
        return Unpooled.wrappedBuffer(buff);
    }

    public static WhyMessage getClientWhyMessage(WhyMessage whyMessage) {
        boolean rewrite = whyMessage.getVariableHeader().isRewrite();
        if(rewrite){
            WhyMessage tempMessage = whyMessage.deepClone();
            tempMessage.getFixedHeader().setVariableFlag(false);
            tempMessage.setVariableHeader(new WhyMessageVariableHeader());
            return tempMessage;
        }
        return whyMessage;
    }
}
