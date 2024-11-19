import { get, post } from "@/request/http/http";

export const getServerAddress = (data:any) => post("/api/nameserver/server/list", data);// 游戏端获取节点信息列表,以便同netty节点建立tcp长连接
export const sendMessage = (data:any) => post("/app/online-debug/send-message", data); // 发送消息
export const createConnect = (data:any) => post("/app/online-debug/connect", data);// 创建连接
export const breakoffCnnect = (data:any) => post("/app/online-debug/disconnect", data); // 断开连接