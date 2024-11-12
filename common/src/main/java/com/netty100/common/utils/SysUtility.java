package com.netty100.common.utils;

import com.netty100.common.constants.CommonConstants;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

/**
 * @author yewenhai
 * @version 1.0.0, 2022/3/23
 * @since 1.0.0, 2022/3/23
 */
public class SysUtility {
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static boolean isNotEmpty(Object obj){
        return !isEmpty(obj);
    }

    public static boolean isEmpty(Object obj) {
        if(obj!=null){
            if (obj instanceof String){
                return ((String)obj).trim().equals("");
            }else if (obj instanceof Map){
                return ((Map)obj).isEmpty()||((Map)obj).size()==0;
            }else if (obj instanceof List){
                return ((List)obj).isEmpty()||((List)obj).size()==0;
            }else if (obj instanceof StringBuffer){
                return ((StringBuffer)obj).toString().trim().equals("");
            }
            return false;
        }
        return true;
    }

    /**
     * 异常信息截取
     */
    public static String getErrorMsg(Exception e){
        StringWriter sw = new StringWriter();
        try(PrintWriter pw = new PrintWriter(sw);){
            e.printStackTrace(pw);
        }
        String content = sw.toString();
        return content.split("\r\n")[0];
//        String regEx = "Caused by:(.*)";
//        Pattern pat = Pattern.compile(regEx);
//        Matcher mat = pat.matcher(logContent);
//        String str = mat.group(1);
//        return str;
    }

    /**
     * 获取本机内网IP地址方法
     * @return
     */
    public static String getHostIp(){
        try{
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            while (allNetInterfaces.hasMoreElements()){
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()){
                    InetAddress ip = (InetAddress) addresses.nextElement();
                    if (ip != null
                            && ip instanceof Inet4Address
                            && !ip.isLoopbackAddress() //loopback地址即本机地址，IPv4的loopback范围是127.0.0.0 ~ 127.255.255.255
                            && ip.getHostAddress().indexOf(":")==-1){
                        return ip.getHostAddress();
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获得外网IP
     * @return 外网IP
     */
    private static String getInternetIp(){
        try{
            Enumeration<NetworkInterface> networks = NetworkInterface.getNetworkInterfaces();
            InetAddress ip = null;
            Enumeration<InetAddress> addrs;
            while (networks.hasMoreElements())
            {
                addrs = networks.nextElement().getInetAddresses();
                while (addrs.hasMoreElements())
                {
                    ip = addrs.nextElement();
                    if (ip != null
                            && ip instanceof Inet4Address
                            && ip.isSiteLocalAddress()
                            && !ip.getHostAddress().equals(getHostIp()))
                    {
                        return ip.getHostAddress();
                    }
                }
            }
            // 如果没有外网IP，就返回内网IP
            return getHostIp();
        } catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public static String getCtxRemoteIp(ChannelHandlerContext ctx){
        String[] addr = getChannelAddr(ctx);

        return addr[2];
    }

    public static String[] getChannelAddr(ChannelHandlerContext ctx){
        if(SysUtility.isEmpty(ctx)){
            return new String[]{"0","0","0","0"};
        }
        String localAddress = "";
        String localPort = "";
        String remoteAddress = "";
        String remotePort = "";

        Channel socketChannel = ctx.channel();
        InetSocketAddress socketAddress = (InetSocketAddress)socketChannel.localAddress();
        if(isNotEmpty(socketAddress)){
            localPort = socketAddress.getPort() + "";
            localAddress = SysUtility.isNotEmpty(socketAddress.getAddress()) ? socketAddress.getAddress().getHostAddress() : "-1";
        }

        InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
        if(isNotEmpty(insocket)){
            remoteAddress = SysUtility.isNotEmpty(insocket.getAddress()) ? insocket.getAddress().getHostAddress() : "-1";
            remotePort = insocket.getPort() + "";
        }

        return new String[]{localAddress, localPort, remoteAddress, remotePort};
    }

    public static String getSysDateWithMis(){
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS").format(LocalDateTime.now());
//        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSsss").format(new Date());
    }

    public static String getSysDate() {
        return getDateFormat().format(new Date());
    }

    public static SimpleDateFormat getDateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    public static void setCurrentProducerChannel(Channel channel){
        InheritableThreadLocalManager.setAttribute(CommonConstants.CURRENT_CHANNEL, channel);
    }

    public static Channel getCurrentProducerChannel(){
        return (Channel) InheritableThreadLocalManager.getAttribute(CommonConstants.CURRENT_CHANNEL);
    }

    public static void destoryCurrentInheritableThreadLocal(){
        InheritableThreadLocalManager.destoryValue();
    }

    public static void setCurrentClientMessageId(Long messageId){
        InheritableThreadLocalManager.setAttribute(CommonConstants.CURRENT_MESSAGE_ID, messageId);
    }

    public static Long getCurrentClientMessageId(){
        return (Long) InheritableThreadLocalManager.getAttribute(CommonConstants.CURRENT_MESSAGE_ID);
    }
}
