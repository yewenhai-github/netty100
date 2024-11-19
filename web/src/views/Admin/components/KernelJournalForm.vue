<template>
    <a-form layout="inline" :model="form" :label-col="{style: {width: '70px'}}">
        <!-- <a-form-item label="用户ID" class="kernel-form-item-label">
            <a-input class="kernel-form-item-value" style="width: 300px" v-model:value="form.userId" />
        </a-form-item> -->
        <a-form-item label="本地地址" class="kernel-form-item-label">
            <a-input class="kernel-form-item-value" style="width: 160px" v-model:value="form.localAddress" />
        </a-form-item>
        <a-form-item label="检查点" class="kernel-form-item-label">
            <a-input class="kernel-form-item-value" style="width: 160px" v-model:value="form.logPoint" />
        </a-form-item>
        <!-- <a-form-item label="设备标识" class="kernel-form-item-label">
            <a-input class="kernel-form-item-value" style="width: 300px" v-model:value="form.deviceId" />
        </a-form-item> -->
        <a-form-item label="记录时间" class="kernel-form-item-label">
            <a-range-picker
                class="kernel-form-item-value"
                style="width: 330px"
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
        <a-form-item class="kernel-form-item-label">
            <div class="kernel-form-item-btns">
                <a-button type="primary" @click="search">
                    <template #icon><SearchOutlined /></template>
                    搜索
                </a-button>
                <a-button style="margin-left: 15px" @click="resetForm">
                    <template #icon><RedoOutlined /></template>
                    重置
                </a-button>
            </div>
        </a-form-item>
    </a-form>
</template>

<script lang="ts">
    import {defineComponent, reactive, toRefs, onMounted} from "vue";
    import {SearchOutlined, RedoOutlined} from "@ant-design/icons-vue";
    import {message, Modal} from 'ant-design-vue';
	import dayjs from "dayjs";
    import {clone} from "@/assets/js/common"

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
                toTime: "",
                form: {
                    rangeModel: [],
                    deviceId: "",
                    startTime: "",
                    endTime: "",
                    userId: "",
                    localAddress: "",
                    logPoint: "",
                }
            });

            

            onMounted(() => {
                search();
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

            const search = () => {
                let req:any = Object.assign(clone(state.form), {pageNum: 0, pageSize: 0, startTime: state.form.rangeModel[0], endTime: state.form.rangeModel[1]});
                delete req.rangeModel;
                if(req.localAddress && !/^((25[0-5]|2[0-4]\d|((1\d{2})|([1-9]?\d)))\.){3}(25[0-5]|2[0-4]\d|((1\d{2})|([1-9]?\d)))$/.test(state.form.localAddress)) {
                    message.warning("请填写正确的本地地址");
                    return; 
                }
                contex.emit("search", req);
            }

            const resetForm = () => {
                for(let key in state.form) {
                    state.form[key] = "";
                }
                state.form.rangeModel = [];
            }

            return {
                ...toRefs(state),
                onOpenChange,
                onCalendarChange,
                onChangeTimer,
                disabledEndDate,
                search,
                resetForm
            }
        }
    })
</script>

<style lang="less" scoped>
    .kernel-form-item-label {
        margin-top: 15px;
    }
    .kernel-form-item-btns {
        padding-left: 90px;
    }
</style>