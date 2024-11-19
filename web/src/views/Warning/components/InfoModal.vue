<template>
    <a-modal class="view-dialog-modal" v-model:visible="infoModal.state" :footer="{}" :maskClosable="false">
        <ul>
            <p>App端 : </p>
            <li v-for="(li, i) of thresholdSourceData.client" :key="i">{{li.shortName}} => {{li.title}}</li>
            <p>服务器 : </p>
            <li v-for="(li, i) of thresholdSourceData.server" :key="i">{{li.shortName}} => {{li.title}}</li>
            <p>内核 : </p>
            <li v-for="(li, i) of thresholdSourceData.kernel" :key="i">{{li.shortName}} => {{li.title}}</li>
        </ul>
    </a-modal>
</template>

<script lang="ts">
    import { defineComponent, reactive, toRefs } from "vue";
    import {getThresholdData} from "@/request/admin/warning"
    export default defineComponent({
        setup() {
            const state = reactive({
                thresholdSourceData: {},
                infoModal: {
                    state: false
                }
            });

            const open = () => {
                state.infoModal.state = true;
            }

            const getThresholdList = async () => { // 获取阈值列表
                const data:any = await getThresholdData({});
                if(!data.data) {
                    return;
                }
                state.thresholdSourceData = data.data;
            }

            getThresholdList();

            return {
                ...toRefs(state),
                open
            }
        },
    })
</script>

<style lang="less" scoped>
    .view-dialog-modal {
        ul {
            p {
                margin: 10px 0px;
            }
            li {
                margin-left: 20px;
            }
        }
    }
</style>
