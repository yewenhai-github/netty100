<template>
    <div class="bg">
        <div class="fontend_container">
            <div class="view">
                <div class="view-title">
                    <PageTitle name="在线调试" />
                </div>
                <div class="view-con">
                    <div class="con-form">
                        <OnlineToolsForm 
                            ref="formRef"
                            :connectState="connectState"
                            @onSend="onSend"/>
                        <div class="form-item-chat">
                            <a-form style="width: 100%" layout="inline" :label-col="{style: {width: '90px'}}">
                                <a-form-item label="对话内容" style="width: 100%">
                                    <ChatBox 
                                        ref="chatBoxRef"
                                        :chatList="chatList" />
                                </a-form-item>
                            </a-form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script lang="ts">
    import {defineComponent, reactive, toRefs, createVNode, nextTick} from "vue";
    import PageTitle from "@/components/PageTitle/PageTitle.vue";
    import ChatBox from "@/components/ChatBox/ChatBox.vue";
    import OnlineToolsForm from "./components/OnlineToolsForm.vue";
    import {clone, Idate} from "@/assets/js/common";
    import {wsSend, wsSetCallback} from "@/request/socket/index";

    let fmtStr:string = "yyyy-MM-dd hh:mm:ss:SSS";

    let codeMap:any = {
            "101": "客户端连接成功",
            "102": "客户端心跳确认",
            "103": "客户端消息处理失败",
            "104": "客户端连接正常断开",
            "105": "客户端连接异常断开",
            "106": "客户端连接心跳断开",
            "107": "客户端连接未认证",
            "108": "客户端认证失败，设备密码不能为空",
            "109": "客户端认证失败，设备未注册",
            "110": "客户端认证失败，设备密码错误",
            "111": "客户端认证失败，静态服务器访问失败",
            "112": "客户端配置失败，消息协议客户端来源不能小于0",
            "113": "客户端配置失败，消息协议服务器来源不能小于0",
            "114": "客户端配置失败，消息协议未配置",
            "115": "服务器不在线",
            "116": "用户强制退出，用户已在其他pad登录",
            "117": "客户端配置不正确，消息与配置不一致",
            "201": "服务器连接成功",
            "202": "服务器心跳确认",
            "203": "服务器消息处理失败",
            "204": "服务器连接正常断开",
            "205": "服务器连接异常断开",
            "900": "未知错误",
            "901": "消息不合法，无法解析",
            "902": "消息不能为空",
            "903": "此消息方式未实现",
        };

    export default defineComponent ({
        components: {
            PageTitle,
            ChatBox,
            OnlineToolsForm
        },
        setup() {
            const state = reactive({
                formRef: null,
                chatBoxRef: null,
                chatList: [] as any,
                connectState: 0,
            });

            const onSend = (req:any) => {
                state.chatList.push({
                    name: "客户端",
                    time: new Idate(new Date(), fmtStr).getFmtTime(),
                    content: req.type == 0 ? "创建连接" : req.type == 1 ? req.message : "断开连接",
                    flag: 0
                })
                wsSend(JSON.stringify(req));
            }

            wsSetCallback((res:any) => {
                if(res == "1") return;
                state.connectState = 1;
                state.chatList.push({
                    name: "服务器",
                    time: new Idate(new Date(), fmtStr).getFmtTime(),
                    content: /^[0-9]{1,3}$/.test(res) ? codeMap[res] : res || "null",
                    // 1服务器  0客户端
                    flag: 1
                });
                (state.chatBoxRef as any).scrollBottom();
            });

            return {
                ...toRefs(state),
                onSend
            }
        }
    })
</script>

<style lang="less" scoped>
    .view {
        padding: 15px 20px;
        .view-con {
            background-color: #ffffff;
            border-radius: 5px;
            margin-top: 15px;
            padding: 15px 20px;
            .form-item-chat {
                width: 100%;
                display: flex;
                margin-top: 15px;
            }
        }
    }
</style>
