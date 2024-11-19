// import { socketUrl } from "@/request/warning/index";
import {socketUrl} from "../config";

let baseUrl:any = "";

// const socketUrl = "ws://qa-netty100-api.netty100.com:8080/websocket/";

const user = () => {
    return window.sessionStorage.user ? JSON.parse(window.sessionStorage.user) : {};
}

let ws:any = null;

/**
 * 心跳时间
 */
const heartDate:any = 10000;

/**
 * 心跳定时器
 */
let heartTimer:any = null;
let headerServerTimer:any = null;

/**
 * 避免重复连接
 */
let reconnectLock:any = false;

/**
 * 回调函数
 */
let global_callback:any = null;

const reconnectWs = () => {
    if(reconnectLock) return;
    reconnectLock = true;
    clearHeartTimer();
    const timer = setTimeout(() => {
        ws = null;
        wsCreate();
        reconnectLock = false
    }, heartDate);
}

const clearHeartTimer = () => {
    clearTimeout(heartTimer);
    clearTimeout(headerServerTimer);
}

const heartStart = () => {
    /**
     * 每10S发送一条消息。
     * 如果发送消息20S内没有的到服务器的响应就认为socket已经断开。
     * 清空ws
     * 重新连接
     */
    heartTimer = setTimeout(() => {
        wsSend("1");
        headerServerTimer = setTimeout(() => {
            ws.close();
        }, heartDate)
    }, heartDate * 2)
}

const disposeResult = (result:any) => {
    const data = JSON.parse(result.data);
    typeof global_callback == "function" && global_callback(data);
}

const setUrl = (url:string) => {
    baseUrl = url;
}

const getUrl = () => {
    if(baseUrl == "") baseUrl = socketUrl + user().id;
    return baseUrl;
}

const wsCreate = () => {
    if(ws == null || typeof ws !== "object") {
        initWebSocket();
    }
}

// 出屎化socket对象
const initWebSocket = () => {
    if(user().id) {
        ws = new WebSocket(getUrl());
    } else {
        ws = null;
        return;
    }

    ws.onmessage = function (e:any) {
        websocketOnMessage(e);
    };

    ws.onclose = function (e:any) {
        reconnectWs();
    };

    ws.onopen = function () {
        websocketOnOpen();
    };

    ws.onerror = function () {
        reconnectWs();
    };
}

// 发送数据
const wsSend = (sendParams:any) => {
    if(ws.readyState === ws.OPEN) {
        websocketSend(sendParams);
    } else { // 正在开启  等待中 -> 延迟发送
        const timer:any = setTimeout(() => {
            wsSend(sendParams);
        }, 1000);
    }
}

// 关闭
const wsClose = () => {
    ws.close();
}

// 设置回调函数
const wsSetCallback = (callback:any) => {
    global_callback = callback;
}

// 接收数据
const websocketOnMessage = (result:any) => {
    disposeResult(result);
    clearHeartTimer();
    heartStart();
}

// 发送数据
const websocketSend = (sendParams:any) => {
    ws.send(sendParams);
}

const websocketOnOpen = () => {
    /**
     * 连接成功之后每隔一段时间发送一次心跳
     */
     clearHeartTimer();
     heartStart();
}

export { wsSend, wsCreate, wsClose, wsSetCallback, setUrl };