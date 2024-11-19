<template>
    <div class="bg">
        <div class="fontend_container">
            <div class="view">
                <div class="view-title">
                    <PageTitle name="连接日志" />
                </div>
                <div class="view-table">
                    <div class="view-table-client">
                        <ConnectForm 
                            ref="searchRef"
                            @search="search"/>
                        <JournalTable
                            :pagination="table.pagination"
                            :columns="table.columns"
                            :loading="table.loading"
                            :dataList="table.list"
                            @change="tableChange" />
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script lang="ts">
    import { defineComponent, reactive, toRefs } from "vue";
    import ConnectForm from "./components/ConnectForm.vue";
    import JournalTable from "./components/JournalTable.vue";
    import {getData} from "@/request/Journal/connect";
    import {clone} from "@/assets/js/common.js";
    import {message, Modal} from 'ant-design-vue';
    import {useAppStore} from "@/store/index";
    import PageTitle from "@/components/PageTitle/PageTitle.vue";

    const width_4 = "160px";

    export default defineComponent ({
        components: {
            ConnectForm,
            JournalTable,
            PageTitle
        },
        setup() {

            const store = useAppStore();

            const state = reactive({
                apiInfoMsg: "api接口响应异常，请重试",
                searchRef: null,
                table: {
                    loading: false,
                    pagination: {
                        current: 1,
                        pageSize: 10,
                        total: 0,
                    },
                    columns: [
                        {title: "序号", align: "center", dataIndex: "sque", fixed: true, width: "70px"},
                        {title: "用户名", align: "center", dataIndex: "userId", width: "200px"},
                        {title: "netty节点本地地址", align: "center", dataIndex: "localAddress", width: "200px"},
                        {title: "netty节点本地绑定端口号", align: "center", dataIndex: "localPort", width: "230px"},
                        {title: "消息目的地", align: "center", dataIndex: "messageDestDesc", width: width_4},
                        {title: "消息源", align: "center", dataIndex: "messageSourceDesc", width: width_4},
                        {title: "远程连接地址", align: "center", dataIndex: "remoteAddress", width: width_4},
                        {title: "远程端口号", align: "center", dataIndex: "remotePort", width: width_4},
                        {title: "日志内容", align: "center", dataIndex: "logContent", width: width_4},
                        {title: "日志点", align: "center", dataIndex: "logPoint", width: width_4},
                        {title: "日志时间", align: "center", dataIndex: "logTime", width: width_4},
                    ],
                    list: [],
                },
            });

            const search = (val:any) => {// 搜索回调函数
                let data:any = clone(val);
                data.pageNum = state.table.pagination.current;
                data.pageSize = state.table.pagination.pageSize;
                for(let key in data) {
                    if(!data[key]) delete data[key];
                }
                getList(data);
            }

            const getList = async (req:any) => {// 获取页面数据
                state.table.loading = true;
                let timer = setTimeout(() => {
                    state.table.loading = false;
                }, store.loadingTime);
                const data:any = await getData(req);
                state.table.loading = false;
                if(!data.data) {
                    message.warning(data.message || state.apiInfoMsg);
                    return;
                }
                state.table.list = data.data.list;
                state.table.pagination.total = data.data.total;
            }

            const tableChange = (pagination:any) => { // 分页
                state.table.pagination.pageSize = pagination.pageSize;
                state.table.pagination.current = pagination.current;
                state.searchRef.search(); // 参数太多了 通过子组件函数调用查询
            }

            return {
                ...toRefs(state),
                tableChange,
                search
            }
        }
    })
</script>

<style lang="less" scoped>
     .bg {
         background-color: #f6fafb;
     }
</style>
