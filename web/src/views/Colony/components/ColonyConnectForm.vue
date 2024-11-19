<template>
    <a-form layout="inline" :model="form" :label-col="{style: {width: '80px'}}">
        <a-form-item label="节点名称" name="serverId" class="connect-form-label">
            <a-select
                v-model:value="form.serverId"
                class="connect-form-item"
                placeholder="请选择节点">
                <a-select-option
                    v-for="item of nodeList" 
                    :key="item.id" 
                    :value="item.id"
                    :label="item.intranetIp">
                    <span style="display: inline-block; width: 100%">
                        {{item.intranetIp && item.intranetIp}}
                    </span>
                </a-select-option>
            </a-select>
        </a-form-item>
        <a-form-item label="连接时间" class="connect-form-label" name="rangeModel">
            <a-range-picker 
                class="connect-form-item"
                show-time
                :allowClear="false"
                v-model:value="form.rangeModel" 
                value-format="YYYY-MM-DD HH:mm:ss" />
        </a-form-item>
        <!-- <a-form-item label="远程地址" name="ip" class="connect-form-label">
            <a-input class="connect-form-item" v-model:value="form.ip" />
        </a-form-item> -->
        <a-form-item class="connect-form-item connect-form-label" v-show="moreCondState">
            <div class="connect-form-item-btns">
                <a-button type="primary" @click="search">
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
        <a-form-item label="用户ID" class="connect-form-label" v-if="tableType == 'client' && !moreCondState" name="userId">
            <a-input class="connect-form-item" v-model:value="form.userId" />
        </a-form-item>
        <a-form-item label="消息源" class="connect-form-label" v-show="!moreCondState">
            <a-select
                class="connect-form-item"
                v-model:value="form.messageSource">
                <a-select-option v-for="item of sourceSelectList" :value="item.protocolCode" :key="item.id">{{item.protocolName}}</a-select-option>
            </a-select>
        </a-form-item>
        <a-form-item label="消息目的地" class="connect-form-label" v-show="!moreCondState">
            <a-select
                class="connect-form-item"
                v-model:value="form.messageDest">
                <a-select-option v-for="item of deptSelectList" :value="item.protocolCode" :key="item.id">{{item.protocolName}}</a-select-option>
            </a-select>
        </a-form-item>
        <a-form-item label="消息类型" class="connect-form-label" v-show="!moreCondState">
            <a-select
                class="connect-form-item"
                v-model:value="form.messageType">
                <a-select-option v-for="item of typeSelectList" :value="item.protocolCode" :key="item.id">{{item.protocolName}}</a-select-option>
            </a-select>
        </a-form-item>
        <a-form-item label="远程地址" name="ip" class="connect-form-label" v-show="!moreCondState">
            <a-input class="connect-form-item" v-model:value="form.ip" />
        </a-form-item>
        <!-- <a-form-item label="连接标识" class="connect-form-label" v-show="!moreCondState">
            <a-input class="connect-form-item" v-model:value="form.channelKey" />
        </a-form-item> -->
        <!-- <a-form-item label="连接时间" class="connect-form-label" name="rangeModel" v-show="!moreCondState">
            <a-range-picker 
                class="connect-form-item"
                show-time
                :allowClear="false"
                v-model:value="form.rangeModel" 
                value-format="YYYY-MM-DD HH:mm:ss" />
        </a-form-item> -->
        <a-form-item class="connect-form-item connect-form-label" v-show="!moreCondState">
            <div class="connect-form-item-btns">
                <a-button type="primary" @click="search">
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
</template>

<script lang="ts">
    import { defineComponent, reactive, toRefs, computed, watch, onMounted } from "vue";
    import { SearchOutlined, RedoOutlined } from "@ant-design/icons-vue";
    import {message, Modal} from 'ant-design-vue';
    import {getAllSelectData} from "@/request/admin/msgAgreement";
    import {clone, Idate} from "@/assets/js/common";

    let idate = new Idate(new Date(), "yyyy-MM-dd");


    export default defineComponent ({
        components: {
            SearchOutlined,
            RedoOutlined,
        },
        props: {
            tableType: {
                type: String,
                default: "client"
            },
            colonyId: {
                type: String,
                dafault: "",
            },
            nodeList: {
                type: Array,
                default: () => []
            },
        },
        emits: ["search"],
        setup(props, contex) {

            const state = reactive({
                apiInfoMsg: "api接口响应异常，请重试",
                moreCondState: true,
                form: {
                    ip: "",
                    userId: "",
                    serverId: "",
                    rangeModel: [] as any,
                    messageSource: "",
                    messageDest: "",
                    messageType: "",
                },
                colonySelectModel: "",
                colonySelectList: [] as any,
                sourceSelectList: [] as any,
                deptSelectList: [] as any,
                waySelectList: [] as any,
                typeSelectList: [] as any,
            });
            
            watch(() => props.colonyId, (val:any) => { // 监听集群下拉变化
                state.form.serverId = "";
            });

            const changeNodeSelect = (val:string) => {
                disposeIntranetIpChange(val);
            }

            const disposeIntranetIpChange = (val:string) => { // 处理用户点击的IP地址
                for(let i = 0; i < props.nodeList.length; i++) {
                    if(props.nodeList[i].intranetIp == val) {
                        state.form.serverId = props.nodeList[i].id;
                        search();
                    }
                }
            }

            const initRangeTime = () => {  // 初始化查询时间
                let startTime:any = idate.getYear() + "-" + idate.getMonth() + "-" + idate.getDate() + " 00:00:00";
                let endTime:any = idate.getYear() + "-" + idate.getMonth() + "-" + idate.getDate() + " 23:59:59";
                state.form.rangeModel = [startTime, endTime];
            }

            const search = () => {
                if(!state.form.serverId) {
                    contex.emit("search", null);
                    message.warning("请选择节点");
                    return; // 没有serverId不请求 否则会报错
                }
                let req:any = clone(state.form);
                req.start = state.form.rangeModel[0];
                req.end = state.form.rangeModel[1];
                delete req.rangeModel;
                delete req.ip;
                if(state.form.ip) req.remoteIp = state.form.ip;
                contex.emit("search", req);
            }

            const getSelectList = async () => { // 获取消息相关下拉框数据
                const selectItem:object = {protocolCode: "", id: "", protocolName: ""};
                const data:any = await getAllSelectData({});
                const {
                    message_source,
                    message_way,
                    message_dest,
                    message_type
                } = data.data;
                state.waySelectList = [selectItem, ...message_way];
                state.typeSelectList = [selectItem, ...message_type];
                state.deptSelectList = [selectItem, ...message_dest];
                state.sourceSelectList = [selectItem, ...message_source];
            }

            getSelectList();

            const resetForm = () => {
                for(let key in state.form) {
                    state.form[key] = "";
                }
                state.form.rangeModel = [];
                // initRangeTime();
            }

            // onMounted(() => {
            //     initRangeTime();
            // });

            return {
                ...toRefs(state),
                resetForm,
                search,
                changeNodeSelect,
            }
        }
    })
</script>

<style lang="less" scoped>
    .connect-form-item {
        width: 300px;
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
    .connect-form-label {
        margin-top: 15px;
    }
</style>
