package com.netty100.test.utils;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

/**
 * @author yewenhai
 * @version 1.0.0, 2022/3/23
 * @since 1.0.0, 2022/3/23
 */
public class ByteBufferUtility {

    public static void main(String[] args) {
        String mac = "AA:AA:AA:AA:AA:AA";
        byte[] macs = new byte[18];
        System.out.println(mac.getBytes().length);
        Long a = 866523046038075l; //11 0001 0100 0001 1001 0001 1110 0101 1001 0100 1010 0011 1011

        ByteBufferUtility util = new ByteBufferUtility();

        byte message_way = 12; //：消息方式 1：c2s、2：s2c、3：c2p-idle、4：p2c-idle（1个字节）
        byte message_source = 11; //：来源，客户端应用唯一标识（1字节）  1-Math 2-English 3-Chinese
        byte message_dest = 122; //：目的，后端业务集群唯一标识（1字节）  1-Math 2-English 3-Chinese
        byte message_type = 112; //：消息类型：1在线单用户 2在线多用户 3在线全用户 4离线单用户 5离线多用户 6离线全用户（1字节）
        Long message_id = System.currentTimeMillis() * 1000000L + System.nanoTime() % 1000000L; //：本次消息的唯一值，精确到纳秒（8个字节）
        Long user_id = 9223372036854775807l; //：用户Id主键（8个字节）
        byte[] body = "{\"code\",\"200\"}".getBytes();//自定义，可以是xml、json、object、mqtt、protobuf、edifact等自定义的系列化对象

//        十进制：9223372036854775807l（长度19位）1000000000000000000000000000000000000000000000000000000000000000
//        十进制：9223372036854776000l
//        二进制：1000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000（长度16*4位=64位）

        //这里的长度，存储字节长度的长度，有几个字节值就是几
        Integer head_length = (1 + 1 + 1 + 1 + 8 + 8);
        Integer message_length = head_length + body.length; //：消息长度（4个字节）
        System.out.println("head_length："+head_length + " body_length："+body.length);

        ByteBuffer buff = ByteBuffer.allocate(4 + message_length);
        buff.putInt(message_length);
        buff.put(message_way);
        buff.put(message_source);
        buff.put(message_dest);
        buff.put(message_type);
        buff.putLong(message_id);
        buff.putLong(user_id);
        buff.put(body);

        buff.flip(); //转换为写出模式
//        head.putInt(message_way).array();
//        contentbuf.position(0);
//        contentbuf.limit(4);

        int r_message_length = buff.getInt();
        int r_message_way = buff.get();
        int r_message_source = buff.get();
        int r_message_dest = buff.get();
        int r_message_type = buff.get();
        Long r_message_id = buff.getLong();
        Long r_user_id = buff.getLong();

        ByteBuffer r_body = buff.get(new byte[body.length]);

        System.out.println("message_length（"+ message_length +"," + r_message_length+"）");
        System.out.println("message_way（"+ message_way +"," + r_message_way+"）");
        System.out.println("message_source（"+ message_source +"," + r_message_source+"）");
        System.out.println("message_dest（"+ message_dest +"," + r_message_dest+"）");
        System.out.println("message_type（"+ message_type +"," + r_message_type+"）");
        System.out.println("message_id（"+ message_id +"," + r_message_id+"）");
        System.out.println("r_user_id（"+ r_user_id +"," + r_user_id+"）");
        System.out.println("body（"+ new String(body) +"," + util.byteBufferToString(r_body)+"）");


        System.out.println("position:" + buff.position() + "\t limit:" + buff.limit() + "\t capacity:" + buff.capacity());
    }




    public String byteBufferToString(ByteBuffer buffer) {

        Charset charset = Charset.forName("utf-8");
        CharBuffer charBuffer = charset.decode(buffer);
        String s = charBuffer.toString();
        return s;
//        CharBuffer charBuffer = null;
//        try {
//            Charset charset = Charset.forName("UTF-8");
//            CharsetDecoder decoder = charset.newDecoder();
//            charBuffer = decoder.decode(buffer);
//            buffer.flip();
//            return charBuffer.toString();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            return null;
//        }
    }


    /**
     * int到byte[] 由高位到低位
     * @param i 需要转换为byte数组的整行值。
     * @return byte数组
     */
    public byte[] convertInt2ByteArray(int i) {
        byte[] result = new byte[4];
        result[0] = (byte)((i >> 24) & 0xFF);
        result[1] = (byte)((i >> 16) & 0xFF);
        result[2] = (byte)((i >> 8) & 0xFF);
        result[3] = (byte)(i & 0xFF);
        return result;
    }

    public byte[] convertInt2Byte(int i) {
        byte[] result = new byte[1];
        result[0] = (byte)((i >> 24) & 0xFF);
        return result;
    }

    public byte[] convertLongToBytes(long l) {
//        byte[] result = new byte[8];
//        for (int i = 7; i >= 0; i--) {
//            result[i] = (byte)(l & 0xFF);
//            l >>= 8;
//        }
//        return result;
        byte[] result = new byte[8];
        result[0] = (byte)((l >> 56) & 0xFF);
        result[1] = (byte)((l >> 48) & 0xFF);
        result[2] = (byte)((l >> 40) & 0xFF);
        result[3] = (byte)((l >> 32) & 0xFF);
        result[4] = (byte)((l >> 24) & 0xFF);
        result[5] = (byte)((l >> 16) & 0xFF);
        result[6] = (byte)((l >> 8) & 0xFF);
        result[7] = (byte)(l & 0xFF);
        return result;
    }


}
