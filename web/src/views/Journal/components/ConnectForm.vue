<template>
    <div class="connect">
        <!-- <div class="connect-name"> -->
            <!-- <p>{{tableType == 'cntClient' || tableType == 'msgClient' ? '客户端' : '应用端'}}</p> -->
        <!-- </div> -->
        <div class="connect-form">
            <a-form layout="inline" :model="form" :label-col="{style: {width: '90px'}}">
                <a-form-item label="用户ID" class="connect-form-item-label">
                    <a-input class="kernel-form-item-value" style="width: 300px" v-model:value="form.userId" />
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
                <a-form-item class="connect-form-item-label" v-show="moreCondState">
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
                <a-form-item label="远程地址" class="connect-form-item-label" v-show="!moreCondState">
                    <a-input class="connect-form-item-value" v-model:value="form.remoteAddress" />
                </a-form-item>
                <a-form-item label="本地地址" class="connect-form-item-label" v-show="!moreCondState">
                    <a-input class="connect-form-item-value" v-model:value="form.localAddress" />
                </a-form-item>
                <a-form-item label="客户端来源" class="connect-form-item-label" v-show="!moreCondState">
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
                <a-form-item class="connect-form-item-label" v-show="!moreCondState">
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
        </div>
    </div>
</template>

<script lang="ts">
    import {defineComponent, reactive, toRefs, computed, onMounted} from "vue";
	import dayjs from "dayjs"
    import {SearchOutlined, RedoOutlined} from "@ant-design/icons-vue";
    import {getAllSelectData} from "@/request/admin/msgAgreement";
    import {Idate} from "@/assets/js/common.js";
    import {message, Modal} from 'ant-design-vue';
    import {clone} from "@/assets/js/common"
    
    let idate = new Idate(new Date(), "yyyy-MM-dd");

    export default defineComponent ({
        components: {
            SearchOutlined,
            RedoOutlined,
        },

        emits: ["search"],
        
        setup(props, contex) {
            const state = reactive({
                disabledCurrent: null,
                fromTime: "",
                moreCondState: true, // true 收起   false 展开
                toTime: "",
                apiInfoMsg: "api接口响应异常，请重试",
                sourceSelectList: [],
                deptSelectList: [],
                form: {
                    userId: "",
                    remoteAddress: "",
                    localAddress: "",
                    rangeModel: [],
                    messageSource: "",
                    messageDest: ""
                },
            });

            const user = computed(() => {
                return JSON.parse(window.sessionStorage.user);
            });

            const getSelectList = async () => { // 获取消息相关下拉框数据
                const selectItem:object = {protocolCode: "", id: "", protocolName: ""};
                const data:any = await getAllSelectData({});
                const {
                    message_source,
                    message_dest,
                } = data.data;
                state.deptSelectList = [selectItem, ...message_dest];
                state.sourceSelectList = [selectItem, ...message_source];
            }

            getSelectList();

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
                let startTime = idate.getYear() + "-" + idate.getMonth() + "-" + idate.getDate() + " 00:00:00";
                let endTime = idate.getYear() + "-" + idate.getMonth() + "-" + idate.getDate() + " 23:59:59";
                state.form.rangeModel = [startTime, endTime];
            }
    
            onMounted(() => {
                initRangeTime();
                search();
            });

            const resetForm = () => {
                for(let key in state.form) {
                    state.form[key] = "";
                }
                state.form.rangeModel = [];
                initRangeTime();
            }

            const search = () => {
                // let data = {
                //     userId: state.form.userId,
                //     startTime: state.form.rangeModel[0],
                //     endTime: state.form.rangeModel[1],
                //     remoteAddress: state.form.remoteAddress,
                //     localAddress: state.form.localAddress,
                //     messageDest: state.form.messageDest,
                //     messageSource: state.form.messageSource,
                //     pageNum: 0,
                //     pageSize: 0,
                // }
                let req:any = Object.assign(clone(state.form), {pageNum: 0, pageSize: 0, startTime: state.form.rangeModel[0], endTime: state.form.rangeModel[1]});
                delete req.rangeModel;
                if(req.remoteAddress && !/^((25[0-5]|2[0-4]\d|((1\d{2})|([1-9]?\d)))\.){3}(25[0-5]|2[0-4]\d|((1\d{2})|([1-9]?\d)))$/.test(req.remoteAddress)) {
                    message.warning("请填写正确的远程链接地址");
                    return; 
                }
                contex.emit("search", req);
            }

            return {
                ...toRefs(state),
                search,
                resetForm,
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
                width: 300px;
            }
            .connect-form-item-btns {
                padding-left: 90px;
            }
        }
    }
</style>
