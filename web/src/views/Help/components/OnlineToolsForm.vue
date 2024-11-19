<template>
    <a-form layout="inline" ref="formRef" :model="form" :label-col="{style: {width: '90px'}}">
        <div class="form-item-cluster">
            <a-form-item label="集群名称" style="width: 100%">
                <a-input-search
                    v-model:value="form.cluster"
                    placeholder="请填写集群名称"
                    enter-button="查询"
                    @search="searchCluster" />
            </a-form-item>
        </div>
        <div class="form-item-msg">
            <a-form-item label="连接地址" class="form-item">
                <div class="form-item_disabledInput">
                    {{form.currServer.intranetIp || ""}}
                </div>
            </a-form-item>
            <a-form-item label="用户编号" name="userId" class="form-item" :rules="rules.userId">
                <a-input v-model:value="form.userId" placeholder="请填写用户编号" />
            </a-form-item>
            <a-form-item label="设备编号" name="deviceId" class="form-item" :rules="rules.deviceId">
                <a-input v-model:value="form.deviceId" placeholder="请填写设备编号" />
            </a-form-item>
            <!-- <a-form-item label="消息类型" class="form-item" name="messageType" :rules="rules.messageType">
                <a-select
                    v-model:value="form.messageType">
                    <a-select-option v-for="item of typeSelectList" :value="item.protocolCode" :key="item.id">{{item.protocolName}}</a-select-option>
                </a-select>
            </a-form-item> -->
        </div>
        <div class="form-item-msg">
            <a-form-item label="客户端来源" class="form-item" name="messageSource" :rules="rules.messageSource">
                <a-select
                    v-model:value="form.messageSource">
                    <a-select-option v-for="item of sourceSelectList" :value="item.protocolCode" :key="item.id">{{item.protocolName}}</a-select-option>
                </a-select>
            </a-form-item>
            <a-form-item label="服务器来源" class="form-item" name="messageDest" :rules="rules.messageDest">
                <a-select
                    v-model:value="form.messageDest">
                    <a-select-option v-for="item of deptSelectList" :value="item.protocolCode" :key="item.id">{{item.protocolName}}</a-select-option>
                </a-select>
            </a-form-item>
            <a-form-item label="序列化方式" class="form-item" name="messageSerialize" :rules="rules.messageSerialize">
                    <a-select
                    v-model:value="form.messageSerialize">
                    <a-select-option v-for="item of serializeList" :value="item.protocolCode" :key="item.id">{{item.protocolName}}</a-select-option>
                </a-select>
            </a-form-item>
            <!-- <a-form-item label="消息事件" class="form-item" name="messageWay" :rules="rules.messageWay">
                <a-select
                    v-model:value="form.messageWay">
                    <a-select-option v-for="item of waySelectList" :value="item.protocolCode" :key="item.id">{{item.protocolName}}</a-select-option>
                </a-select>
            </a-form-item> -->
        </div>
        <div class="form-item-btn">
            <div></div>
            <a-form-item class="form-item">
                <a-button type="primary" @click="send(0)">创建连接</a-button>
                <a-button danger style="margin-left: 15px" @click="send(2)">断开连接</a-button>
            </a-form-item>
        </div>
        <div class="form-item-body">
            <a-form-item label="消息内容" style="width: 100%" name="message">
                <a-textarea v-model:value="form.message" placeholder="请填写消息内容" :rows="4" />
            </a-form-item>
        </div>
        <div class="form-item-btn">
            <div></div>
            <a-form-item class="form-item">
                <a-button type="primary" @click="send(1)">发送</a-button>
                <a-button style="margin-left: 15px" @click="clearMessage">清空</a-button>
            </a-form-item>
        </div>
    </a-form>
</template>

<script lang="ts">
    import {defineComponent, reactive, toRefs, createVNode, nextTick} from "vue";
    import {getAllSelectData} from "@/request/admin/msgAgreement";
    import {clone, Idate} from "@/assets/js/common";
    import {getServerAddress, sendMessage, createConnect, breakoffCnnect} from "@/request/help/onlineTools";
    import {message} from "ant-design-vue";

    // let codeMap:any = {
    //     "101": "客户端连接成功",
    //     "102": "客户端心跳确认",
    //     "103": "客户端消息处理失败",
    //     "104": "客户端连接正常断开",
    //     "105": "客户端连接异常断开",
    //     "106": "客户端连接心跳断开",
    //     "107": "客户端连接未认证",
    //     "108": "客户端认证失败，设备密码不能为空",
    //     "109": "客户端认证失败，设备未注册",
    //     "110": "客户端认证失败，设备密码错误",
    //     "111": "客户端认证失败，静态服务器访问失败",
    //     "112": "客户端配置失败，消息协议客户端来源不能小于0",
    //     "113": "客户端配置失败，消息协议服务器来源不能小于0",
    //     "114": "客户端配置失败，消息协议未配置",
    //     "115": "服务器不在线",
    //     "116": "用户强制退出，用户已在其他pad登录",
    //     "117": "客户端配置不正确，消息与配置不一致",
    //     "201": "服务器连接成功",
    //     "202": "服务器心跳确认",
    //     "203": "服务器消息处理失败",
    //     "204": "服务器连接正常断开",
    //     "205": "服务器连接异常断开",
    //     "900": "未知错误",
    //     "901": "消息不合法，无法解析",
    //     "902": "消息不能为空",
    //     "903": "此消息方式未实现",
    // };

    // let fmtStr:string = "yyyy-MM-dd hh:mm:ss:SSS";

    export default defineComponent({

        props: {
            connectState: {
                type: Number,
                default: 0
            }
        },

        emits: ["changeMessage", "onSend"],

        setup(props, contex) {
            let currServerIndex:number = 0;
            // let connectState:number = 0;
            const state = reactive({
                formRef: null,
                // 0 未创建连接    1 已创建连接    
                sourceSelectList: [] as any,
                deptSelectList: [] as any,
                serializeList: [] as any,
                serverAddress: [] as any,
                waySelectList: [] as any,
                typeSelectList: [] as any,
                form: {
                    messageWay: "1",
                    messageSource: "",
                    messageDest: "",
                    messageType: "1",
                    messageSerialize: "",
                    message: "",
                    userId: "",
                    deviceId: "",
                    currServer: {
                        intranetIp: "",
                        pport: ""
                    } as any
                },
                rules: {
                    userId: [{required: true, pattern: new RegExp(/^[0-9]{1,10}$/), message: "请填写用户编号"}],
                    deviceId: [
                        {required: true, pattern: new RegExp(/^(?!.*-)|^(\s*)$/), message: "设备编号不能为负数"},
                        {required: true, pattern: new RegExp(/^-|[0-9]{1,19}$/), message: "请填写正确的设备编号"}
                    ],
                    // messageType: [{required: true, message: "请选择消息类型"}],
                    messageSource: [{required: true, message: "请选择客户端来源"}],
                    messageDest: [{required: true, message: "请选择服务器来源"}],
                    messageSerialize: [{required: true, message: "请选择序列化方式"}],
                    // messageWay: [{required: true, message: "请选择消息事件"}],
                    // message: [{required: true, message: "请填写消息内容"}]
                }
            });

            const getSelectList = async () => { // 获取消息相关下拉框数据
                const selectItem:object = {protocolCode: "", id: "", protocolName: ""};
                const data:any = await getAllSelectData({});
                const {
                    message_type,
                    message_source,
                    message_way,
                    message_dest,
                    message_serialize
                } = data.data;
                state.typeSelectList = [selectItem, ...message_type];
                state.waySelectList = [selectItem, ...message_way];
                state.deptSelectList = [selectItem, ...message_dest];
                state.serializeList = [selectItem, ...message_serialize];
                state.sourceSelectList = [selectItem, ...message_source];
            }

            getSelectList();

            const searchCluster = async (val:string) => { // 查找集群
                if(!val) {
                    state.form.currServer = {intranetIp: "", port: ""};
                    message.warn("请填写集群名称");
                    return;
                }
                let result:any = await getServerAddress({cluster: val});
                if(result.data.length == 0) {
                    message.warn("未查询到该集群对应的连接地址");
                    return;
                }
                state.serverAddress = result.data;
                currServerIndex = 0;
                switchCurrServer(false);
            }

            const switchCurrServer = (status:boolean) => { // 当前集群连接报错，切换集群
                if(currServerIndex < state.serverAddress.length) {
                    state.form.currServer = state.serverAddress[currServerIndex] ? state.serverAddress[currServerIndex] : {intranetIp: "", port: ""};
                    currServerIndex++;
                    if(status) {
                        send(0);
                    }
                } else {
                    message.warn("当前集群无其他连接地址");
                }
            }

            const send = (type?:number) => { // 发送消息
                fmtReq((req:any) => {
                    req.type = type;
                    contex.emit("onSend", req);
                }, type);
                if(type == 1) state.form.message = "";
            }

            // const createConnectBtn = () => { // 创建连接
            //     fmtReq((req:any) => {
            //         req.type = 1;
            //     }, 0)
            // }

            // const breakoffConnectBtn = () => { // 断开连接
            //      fmtReq((req:any) => {
            //         req.type = 2;
            //     }, 0)
            // }

            const fmtReq = (callback:any, type?:number) => { // 格式化参数
                // type == 0  不校验    type == 1 校验
                let req:any = clone(state.form);
                req.localAddress = state.form.currServer.intranetIp;
                req.localPort = state.form.currServer.port;
                req.messageId = new Date().getTime();
                delete req.currServer;
                delete req.cluster;
                if(!req.localAddress) {
                    message.warn("请先查询连接地址再发送");
                    return false;
                }
                if(type == 1 && props.connectState == 0) {
                    message.warn("请先创建连接");
                    return false;
                }
                if(type == 1 && !state.form.message) {
                    message.warn("请填写消息内容");
                    return false;
                }
                // if(type == 0) {
                //     callback(req);
                //     return true;
                // }
                (state.formRef as any).validate().then((val:any) => {
                    callback(req);
                    return true;
                }).catch((err:any) => {
                    return false;
                })
            }

            // const send = () => {
            //     if(connectState == 0) {
            //         message.warn("请先创建连接");
            //         return;
            //     }
            //     fmtReq(async (req:any) => {
            //         if(!req) return;
            //         insertMessage(async () => {
            //             req.messageWay = "3";
            //             let result:any = await sendMessage(req);
            //             if(!result) {
            //                 switchCurrServer(true);
            //                 return
            //             }
            //             return result;
            //         }, state.form.message);
            //     }, 1);
            // }

            // const createConnectBtn = () => {
            //     fmtReq(async (req:any) => {
            //         if(!req) return;
            //         insertMessage(async () => {
            //             req.messageWay = "1";
            //             let result = await createConnect(req);
            //             if(!result) return null;
            //             connectState = 1;
            //             return result;
            //         }, "创建连接");
            //     }, 0)
            // }

            // const breakoffConnectBtn = async () => {
            //     let req:any = {
            //         deviceId: state.form.deviceId,
            //         localAddress: state.form.currServer.intranetIp,
            //         localPort: state.form.currServer.port,
            //         userId: state.form.userId
            //     }
            //     insertMessage(async () => {
            //         connectState = 0;
            //         return await breakoffCnnect(req);
            //     }, "连接断开");
            // }

            // const insertMessage = async (callback?:any, clientMessage?:string) => {
            //     contex.emit("changeMessage", {
            //         name: "客户端",
            //         time: new Idate(new Date(), fmtStr).getFmtTime(),
            //         content: clientMessage,
            //         flag: 0
            //     });
            //     let result:any = await callback();
            //     contex.emit("changeMessage", {
            //         name: "服务器",
            //         time: new Idate(new Date(), fmtStr).getFmtTime(),
            //         content: /^[0-9]{1,3}$/.test(result.data) ? codeMap[result.data] : result.data || result.message,
            //         // 1服务器  0客户端
            //         flag: 1
            //     });
            //     if(clientMessage != "创建连接") state.form.message = "";
            // }

            const clearMessage = () => {
                state.form.message = "";
            }

            return {
                ...toRefs(state),
                // createConnectBtn,
                // breakoffConnectBtn,
                searchCluster,
                clearMessage,
                send
            }
        },
    })
</script>


<style lang="less" scoped>
    .form-item-cluster,
    .form-item-msg,
    .form-item-body,
    .form-item-btn {
        width: 100%;
        display: flex;
        margin-top: 15px;
    }
    .form-item-msg .form-item {
        // width: 23.071%;
        width: 31.577%;
        &:not(:first-child) {
            margin-left: 15px;
        }
        .form-item_disabledInput {
            width: 100%;
            height: 32px;
            line-height: 28px;
            padding: 0 11px;
            background-color: #fff;
            border: 1px solid #d9d9d9;
            border-radius: 2px;
            color: rgba(0, 0, 0, 0.5);
            background-color: #f5f5f5;
            border-color: #d9d9d9;
            box-shadow: none;
            cursor: not-allowed;
            opacity: 1;
        }
    }
    .form-item-btn {
        display: flex;
        justify-content: space-between;
    }
</style>