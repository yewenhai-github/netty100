<template>
    <a-form layout="inline" :model="form" :label-col="{style: {width: '90px'}}">
        <a-form-item label="设备ID" class="device-form-item-label">
            <a-input class="device-form-item-value" v-model:value="form.deviceId" />
        </a-form-item>
        <a-form-item label="查询日期" class="device-form-item-label">
            <a-range-picker
                class="device-form-item-value"
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
        <a-form-item class="device-form-item-label">
            <div class="device-form-item-btns">
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
	import dayjs from "dayjs";

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
                    deviceId: "",
                    rangeModel: [],
                    startTime: "",
                    endTime: "",
                    localAddress: ""
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
                contex.emit("search", {
                    deviceId: state.form.deviceId,
                    startTime: state.form.rangeModel[0] || "",
                    endTime: state.form.rangeModel[1] || ""
                });
            }

            const resetForm = () => {
                state.form.deviceId = "";
                state.form.startTime = "";
                state.form.endTime = "";
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
    .admin-page {
        .device {
            .view-item {
                background-color: #ffffff;
                border-radius: 5px;
                padding: 15px 20px;
            }
            .device-title {
                p {
                    margin: 0;
                    font-weight: bold;
                    font-size: 16px;
                }
            }
            .device-table {
                margin-top: 15px;
            }
        }
    }
</style>
