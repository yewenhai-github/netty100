<template>
    <div class="connect">
        <!-- <div class="connect-name"> -->
            <!-- <p>{{tableType == 'cntClient' || tableType == 'msgClient' ? '客户端' : '应用端'}}</p> -->
        <!-- </div> -->
        <div class="connect-form">
            <a-form layout="inline" :model="form" :label-col="{style: {width: '98px'}}">
                <a-form-item label="集群名称" class="connect-form-item-label">
                    <a-select
                        class="connect-form-item-value"
                        v-model:value="form.colonySelectModel"
                        @change="colonySelectChange">
                        <a-select-option v-for="item of colonySelectList" :value="item.id" :key="item.id">{{item.cluster}}</a-select-option>
                    </a-select>
                </a-form-item>
                <a-form-item label="记录时间" class="connect-form-item-label">
                    <a-range-picker
                        class="connect-form-item-value"
                        value-format="YYYY-MM-DD HH:mm:ss"
                        :allowClear="false"
                        v-model:value="form.rangeModel" 
                        :disabled-date="disabledEndDate"
                        show-time
                        @openChange="onOpenChange"
                        @calendarChange="onCalendarChange"
                        @change="onChangeTimer"
                    ></a-range-picker>
                </a-form-item>
                <a-form-item label="内核地址" class="connect-form-item-label">
                    <a-select
                        class="connect-form-item-value"
                        v-model:value="form.nodeSelectModel">
                        <a-select-option v-for="item of nodeSelectList" :value="item.id" :key="item.id">{{item.intranetIp}}</a-select-option>
                    </a-select>
                </a-form-item>
                <a-form-item label="用户编号" class="connect-form-item-label">
                    <a-input class="connect-form-item-value" v-model:value="form.userId" />
                </a-form-item>
                <a-form-item label="消息编号" class="connect-form-item-label">
                    <a-input class="connect-form-item-value" v-model:value="form.messageId" />
                </a-form-item>
                <a-form-item label="设备编号" class="connect-form-item-label" v-show="!moreCondState">
                    <a-input class="connect-form-item-value" v-model:value="form.deviceId" />
                </a-form-item>
                <a-form-item label="消息类型" class="connect-form-item-label"  v-show="!moreCondState">
                    <a-select
                        class="connect-form-item-value"
                        v-model:value="form.messageType">
                        <a-select-option v-for="item of typeSelectList" :value="item.protocolCode" :key="item.id">{{item.protocolName}}</a-select-option>
                    </a-select>
                </a-form-item>
                <a-form-item label="消息事件" class="connect-form-item-label" v-show="!moreCondState">
                    <a-select
                        class="connect-form-item-value"
                        v-model:value="form.messageWay">
                        <a-select-option v-for="item of waySelectList" :value="item.protocolCode" :key="item.id">{{item.protocolName}}</a-select-option>
                    </a-select>
                </a-form-item>
                <a-form-item class="connect-form-item-label" v-show="moreCondState">
                    <div class="connect-form-item-btns">
                        <a-button type="primary" @click="search(true)">
                            <template #icon><SearchOutlined /></template>
                            搜索
                        </a-button>
                        <a-button style="margin-left: 15px" @click="resetForm">
                            <template #icon><RedoOutlined /></template>
                            重置
                        </a-button>
                        <span class="more-btn" @click="moreCondState = !moreCondState">
                            {{moreCondState ? "展开更多" : "折叠"}}
                            <span v-if="moreCondState">
                                <DownOutlined />
                            </span>
                            <span v-else>
                                <UpOutlined />
                            </span>
                        </span>
                    </div>
                </a-form-item>
                <a-form-item label="客户端来源" class="connect-form-item-label"  v-show="!moreCondState">
                    <a-select
                        class="connect-form-item-value"
                        v-model:value="form.messageSource">
                        <a-select-option v-for="item of sourceSelectList" :value="item.protocolCode" :key="item.id">{{item.protocolName}}</a-select-option>
                    </a-select>
                </a-form-item>
                <a-form-item label="服务器来源" class="connect-form-item-label" v-show="!moreCondState">
                    <a-select
                        class="connect-form-item-value"
                        v-model:value="form.messageDest">
                        <a-select-option v-for="item of deptSelectList" :value="item.protocolCode" :key="item.id">{{item.protocolName}}</a-select-option>
                    </a-select>
                </a-form-item>
                <a-form-item label="序列化方式" class="connect-form-item-label" v-show="!moreCondState">
                    <a-select
                        class="connect-form-item-value"
                        v-model:value="form.messageSerialize">
                        <a-select-option v-for="item of serializeSelectList" :value="item.protocolCode" :key="item.id">{{item.protocolName}}</a-select-option>
                    </a-select>
                </a-form-item>
                <a-form-item label="远程地址" class="connect-form-item-label" v-show="!moreCondState">
                    <a-input class="connect-form-item-value" v-model:value="form.remoteAddress" />
                </a-form-item>
                <a-form-item label="记录埋点" class="connect-form-item-label" v-show="!moreCondState">
                    <a-select
                        class="connect-form-item-value"
                        v-model:value="form.logPoint">
                        <a-select-option v-for="item of logPointList" :value="item.value" :key="item.value">{{item.label}}</a-select-option>
                    </a-select>
                </a-form-item>
                <a-form-item class="connect-form-item-label" v-show="!moreCondState">
                    <div class="connect-form-item-btns">
                        <a-button type="primary" @click="search(true)">
                            <template #icon><SearchOutlined /></template>
                            搜索
                        </a-button>
                        <a-button style="margin-left: 15px" @click="resetForm">
                            <template #icon><RedoOutlined /></template>
                            重置
                        </a-button>
                        <span class="more-btn" @click="moreCondState = !moreCondState">
                            {{moreCondState ? "展开更多" : "折叠"}}
                            <span v-if="moreCondState">
                                <DownOutlined />
                            </span>
                            <span v-else>
                                <UpOutlined />
                            </span>
                        </span>
                    </div>
                </a-form-item>
            </a-form>
        </div>
    </div>
</template>

<script lang="ts">
    import {defineComponent, reactive, toRefs, computed, onMounted} from "vue";
	import dayjs from "dayjs"
    import {SearchOutlined, RedoOutlined, DownOutlined, UpOutlined} from "@ant-design/icons-vue";
    import {getColonyBrief, getNodeBriefList} from "@/request/colony/index";
    import {getAllSelectData} from "@/request/admin/msgAgreement";
    import {Idate} from "@/assets/js/common.js";
    import {message, Modal} from 'ant-design-vue';
    import {messageLogPointList} from "@/common/js/journalLogPointList.js";
    
    let idate = new Idate(new Date(), "yyyy-MM-dd");

    export default defineComponent ({
        components: {
            SearchOutlined,
            RedoOutlined,
            DownOutlined,
            UpOutlined
        },

        emits: ["search"],
        
        setup(props, contex) {
            const state = reactive({
                disabledCurrent: null,
                fromTime: "",
                toTime: "",
                apiInfoMsg: "api接口响应异常，请重试",
                moreCondState: true, // true 收起   false 展开
                colonySelectList: [] as any,
                nodeSelectList: [] as any,
                sourceSelectList: [] as any,
                typeSelectList: [] as any,
                waySelectList: [] as any,
                deptSelectList: [] as any,
                serializeSelectList: [] as any,
                form: {
                    deviceId: "",
                    userId: "",
                    messageId: "",
                    messageSource: "",
                    messageType: "",
                    messageWay: "",
                    messageDest: "",
                    messageSerialize: "",
                    remoteAddress: "",
                    rangeModel: [] as any,
                    colonySelectModel: 0,
                    nodeSelectModel: "",
                    logPoint: "",
                },
                // 存放集群的第一个选项
                _ColonySelectModel: 0,
                logPointList: messageLogPointList
            });

            const user = computed(() => {
                return JSON.parse(window.sessionStorage.user);
            });

            const onOpenChange = (status:any) => {
                state.disabledCurrent = null;
            }

            const onCalendarChange = (dates:any) => {
                state.disabledCurrent = dates ? dates[0] : "";
            }

            const onChangeTimer = (dayjs:any, dates:any) => {
                state.fromTime = dates ? dates[0] : "";
                state.toTime = dates ? dates[1] : "";
            }

            const disabledEndDate = (val:any) => {
                if(!state.disabledCurrent) return false
                return val < dayjs(state.disabledCurrent).subtract(1, "M").startOf("day") || val > dayjs(state.disabledCurrent).add(1, "M").endOf("day");
            }

            const initRangeTime = () => {  // 初始化查询时间
                let startTime:any = idate.getYear() + "-" + idate.getMonth() + "-" + idate.getDate() + " 00:00:00";
                let endTime:any = idate.getYear() + "-" + idate.getMonth() + "-" + idate.getDate() + " 23:59:59";
                state.form.rangeModel = [startTime, endTime];
            }

            const getColonyList = async () => { // 获取集群列表
                const data:any = await getColonyBrief({});
                if(!data.data) {
                    message.warning(data.message || state.apiInfoMsg);
                    return;
                }
                state.colonySelectList = data.data;
                state.form.colonySelectModel = data.data[0].id;
                // 保存一下第一次拿到的集群ID。后续初始化需要用
                state._ColonySelectModel = data.data[0].id;
                getNodeList();
                initRangeTime();
                search(true);
            }

            getColonyList();

            const getNodeList = async () => { // 获取节点列表
                const data:any = await getNodeBriefList({colonyId: state.form.colonySelectModel});
                if(!data.data) {
                    message.warning(data.message || state.apiInfoMsg);
                    return;
                }
                state.nodeSelectList = [{id: "", intranetIp: ""}, ...data.data];
            }

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
                state.serializeSelectList = [selectItem, ...message_serialize];
                state.sourceSelectList = [selectItem, ...message_source];
            }

            getSelectList();

            const colonySelectChange = (val:any) => { // 集群下拉框改变
                state.form.nodeSelectModel = "";
                getNodeList();
            }

            // onMounted(() => {
            //     initRangeTime();
            //     search(true);
            // });

            const resetForm = () => {
                for(let key in state.form) {
                    state.form[key] = "";
                }
                state.form.colonySelectModel = state._ColonySelectModel;
                state.form.rangeModel = [];
                initRangeTime();
            }

            const search = (pageStatus:boolean) => {
                let data = {
                	clusterId: state.form.colonySelectModel,
                    start: state.form.rangeModel[0],
                    end: state.form.rangeModel[1],
                    deviceId: state.form.deviceId,
                    messageId: state.form.messageId,
                    messageSource: state.form.messageSource, // 消息源
                    messageType: state.form.messageType, // 消息类型
                    messageWay: state.form.messageWay, // 消息方式
                    messageDest: state.form.messageDest, // 消息目的地
                    messageSerialize: state.form.messageSerialize, // 序列化方式
                    remoteAddress: state.form.remoteAddress,
                    serverId: state.form.nodeSelectModel,
                    logPoint: state.form.logPoint,
                    pageNum: 0,
                    pageSize: 0,
                    userId: state.form.userId
                }

                let numReg = /^[0-9]{1,19}$/;
                let numRegInfo = "请填写1-19位数字组成的"
                if(data.remoteAddress && !/^((25[0-5]|2[0-4]\d|((1\d{2})|([1-9]?\d)))\.){3}(25[0-5]|2[0-4]\d|((1\d{2})|([1-9]?\d)))$/.test(data.remoteAddress)) {
                    message.warning("请填写正确的远程链接地址");
                    return; 
                }
                if(data.deviceId && !numReg.test(data.deviceId)) {
                    message.warning(numRegInfo + "设备标识");
                    return;
                }
                if(data.messageId && !numReg.test(data.messageId)) {
                    message.warning(numRegInfo + "消息标识");
                    return;
                }
                if(data.userId && !numReg.test(data.userId)) {
                    message.warning(numRegInfo + "用户ID");
                    return;
                }
                contex.emit("search", data, pageStatus);
            }

            return {
                ...toRefs(state),
                search,
                resetForm,
                colonySelectChange,
                onOpenChange,
                onCalendarChange,
                onChangeTimer,
                disabledEndDate,
                dayjs
            }
        }
    })
</script>

<style lang="less" scoped>
    .connect {
        margin-top: 20px;
        background-color: #ffffff;
        padding: 15px 20px;
        border-radius: 5px;
        .connect-name {
            p {
                font-size: 16px;
                font-weight: bold;
                margin: 0;
            }
        }
        .connect-form {
            margin-top: 15px;
            .connect-form-item-label{
                margin-top: 15px;
                .more-btn {
                    margin-left: 15px;
                    color: #00A273;
                    cursor: pointer;
                    transition: all .15s linear;
                    &:hover {
                        color: #08B481;
                    }
                    .more-btn-show {
                        transform: rotate(0deg);
                    }
                    .more-btn-hide {
                        transform: rotate(90deg);
                    }
                }
            }
            .connect-form-item-value {
                width: 290px;
            }
            .connect-form-item-btns {
                width: 388px;
                padding-left: 98px;
            }
        }
    }
</style>
