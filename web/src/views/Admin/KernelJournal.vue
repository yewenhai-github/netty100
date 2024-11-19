<template>
<!-- <div class="bg"> -->
        <!-- <div class="fontend_container"> -->
            <!-- <div class="view"> -->
                <!-- <div class="view-title"> -->
    <div class="bg">
        <div class="admin-page fontend_container">
            <div class="kernel view">
                <div class="kernel-title">
                    <PageTitle name="内核日志" />
                </div>
                <div class="kernel-table view-item">
                    <div class="kernel-form">
                        <KernelJournalForm 
                            ref="kernelJournalFormRef"
                            @search="search"/>
                    </div>
                    <div class="kernel-table">
                        <Table 
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
    import {defineComponent, reactive, toRefs, computed} from "vue";
    import PageTitle from "@/components/PageTitle/PageTitle.vue";
    import {getData} from "@/request/admin/kernelJournal";
    import {useAppStore} from "@/store/index";
    import {message, Modal} from 'ant-design-vue';
    import KernelJournalForm from "./components/KernelJournalForm.vue"

    export default defineComponent ({
        components: {
            PageTitle,
            KernelJournalForm
        },
        setup() {

            const store = useAppStore();

            const state = reactive({
                apiInfoMsg: "api接口响应异常，请重试",
                kernelJournalFormRef: null,
                disabledCurrent: null,
                fromTime: "",
                toTime: "",
                table: {
                    loading: false,
                    pagination: {
                        current: 1,
                        pageSize: 10,
                        total: 0,
                    },
                    columns: [
                        {title: "序号", align: "center", dataIndex: "sque"},
                        // {title: "用户名", align: "center", dataIndex: "userId"},
                        // {title: "设备标识", align: "center", dataIndex: "deviceId"},
                        {title: "内核本地地址", align: "center", dataIndex: "localAddress"},
                        {title: "日志内容", align: "center", dataIndex: "logContent", width: "300px"},
                        {title: "检查点", align: "center", dataIndex: "logPoint"},
                        {title: "记录时间", align: "center", dataIndex: "logTime"},
                        // {title: "消息目的地", align: "center", dataIndex: "messageDestDesc"},
                        // {title: "消息源", align: "center", dataIndex: "messageSourceDesc"},
                    ],
                    list: [],
                }
            });

            const user:any = computed(() => {
                return JSON.parse(window.sessionStorage.user);
            });

            const search = (req:any) => {
                req.pageNum = state.table.pagination.current;
                req.pageSize = state.table.pagination.pageSize;
                for(let key in req) {
                    if(!req[key]) delete req[key]
                }
                // req.userId = user.value.id;
                getList(req);
            }

            const getList = async (req:any) => {
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
                state.table.list = data.data.list || [];
                state.table.pagination.total = data.data.total;
            }

            const tableChange = (pagination:any) => { // 列表分页改变
                state.table.pagination.pageSize = pagination.pageSize;
                state.table.pagination.current = pagination.current;
                (state.kernelJournalFormRef as any).search();
            }

            return {
                ...toRefs(state),
                search,
                tableChange
            }
        }
    })
</script>

<style lang="less" scoped>
    .admin-page {
        .kernel {
            .view-item {
                background-color: #ffffff;
                border-radius: 5px;
                padding: 15px 20px;
            }
            .kernel-title {
                p {
                    margin: 0;
                    font-weight: bold;
                    font-size: 16px;
                }
            }
            .kernel-table {
                margin-top: 15px;
            }
        }
    }
</style>