<template>
    <div>
        <div class="colony-item">
            <a-tabs v-model:activeKey="tabsModel">
                <a-tab-pane key="client" tab="客户端">
                    <ColonyConnectTable ref="colonyConnectClientRef" tableType="client" :colonyId="colonyId" :nodeList="nodeList" />
                </a-tab-pane>
                <a-tab-pane key="serve" tab="服务端">
                    <ColonyConnectTable ref="colonyConnectServeRef" tableType="serve" :colonyId="colonyId" :nodeList="nodeList" />
                </a-tab-pane>
            </a-tabs>
        </div>
    </div>
</template>

<script lang="ts">
    import { defineComponent, reactive, toRefs, watch } from "vue";
    import ColonyConnectTable from "./ColonyConnectTable.vue";
    import { getNodeBriefList } from "@/request/colony/index";

    export default defineComponent ({
        components: {
            ColonyConnectTable
        },

        props: {
            colonyId: {
                type: Number,
                default: -1,
            },
        },

        setup(props) {
            const state = reactive({
                tabsModel: "serve",
                colonyConnectClientRef: null,
                colonyConnectServeRef: null,
                nodeList: [],
            });

            watch(() => props.colonyId, (val:any) => {
                getNodeList();
            });

            const changeNodeSelect = (val:string) => {
                (state.colonyConnectClientRef as any).changeNodeSelect(val);
                (state.colonyConnectServeRef as any).changeNodeSelect(val);
            }

            const getNodeList = async () => {
                const data:any = await getNodeBriefList({colonyId: props.colonyId});
                state.nodeList = data.data;
            }

            getNodeList();

            let timer = setTimeout(() => {
                state.tabsModel = "client";
            });

            return {
                ...toRefs(state),
                changeNodeSelect,
            }
        }
    })
</script>

<style lang="less" scoped>
    .colony-header {
        display: flex;
        align-items: center;
        background-color: #ffffff;
        border-radius: 5px;
        padding: 15px 20px;
        .colony-header-name {
            p {
                font-weight: bold;
                font-size: 16px;
                margin: 0;
            }
        }
        .colony-header-search {
            display: flex;
            align-items: center;
            margin-left: 15px;
            p {
                margin: 0px 15px 0px 0px;
                display: inline-block;
            }
        }
    }
    .colony-item {
        margin-top: 15px;
        background-color: #ffffff;
        border-radius: 5px;
    }
</style>

<style>
    .ant-tabs > .ant-tabs-nav {
        padding: 0 20px;
    }
</style>
