<template>
    <div class="admin-page">
        <div class="colony">
            <div class="view-list view-colony">
                <div class="view-list-title view-title">
                    <PageTitle name="集群列表" />
                    <div class="view-title-btn">
                        <span @click="addColony">
                            <PlusOutlined/>
                            新增
                        </span>
                    </div>
                </div>
                <div class="view-list-table view-table">
                    <Table 
                        :pagination="listTable.pagination"
                        :columns="listTable.columns"
                        :loading="listTable.loading"
                        :dataList="listTable.list"
                        :scroll="{y: 500}"
                        @update="colonyUpdate" />
                </div>
            </div>
            <div class="view-relation view-colony">
                <div class="view-relation-title view-title">
                    <PageTitle name="集群权限" />
                    <div class="view-title-btn">
                        <span @click="addColonyRelation">
                            <PlusOutlined/>
                            新增关联
                        </span>
                    </div>
                </div>
                <div class="view-relation-table view-table">
                    <Table 
                        :pagination="relationTable.pagination"
                        :columns="relationTable.columns"
                        :loading="relationTable.loading"
                        :dataList="relationTable.list"
                        :scroll="{y: 500}"
                        @update="relationUpdate"
                        @change="relationTableChange" />
                </div>
            </div>
        </div>
        <div class="modal">
            <ColonyAddColonyModal 
                ref="addColonyModalRef"
                @save="addColonySave" />
            <ColonyAddRelationModal 
                ref="addRelationModalRef"
                @save="addColonyRelationSave" />
        </div>
    </div>
</template>

<script lang="ts">
    import {defineComponent, reactive, toRefs, createVNode, nextTick} from "vue";
    import {PlusOutlined} from "@ant-design/icons-vue";
    import {getColonyData, getColonyRelationData, addColonyData, relationColonyData, updateColonyData} from "@/request/admin/colony";
    import ColonyAddColonyModal from "./components/ColonyAddColonyModal.vue";
    import ColonyAddRelationModal from "./components/ColonyAddRelationModal.vue";
    import {message, Modal} from 'ant-design-vue';
    import {useAppStore} from "@/store/index";
    import PageTitle from "@/components/PageTitle/PageTitle.vue";

    // const pageSizeOptions = ['10', '20', '30'];
    // const showTotal = (total:number, range:number[]) => {
    //     return range[0] + '-' + range[1] + ' 共' + total + '条'
    // }
    export default defineComponent ({
        components: {
            PlusOutlined,
            ColonyAddColonyModal,
            ColonyAddRelationModal,
            PageTitle
        },
        setup() {

            const store = useAppStore();

            const state = reactive({
                apiInfoMsg: "api接口响应异常，请重试",
                addColonyModalRef: null,
                addRelationModalRef: null,
                listTable: {
                    loading: false,
                    pagination: false,
                    columns: [
                        {title: "序号", align: "center", dataIndex: "sque"},
                        {title: "集群名称", align: "center", dataIndex: "cluster"},
                        {title: "集群描述", align: "center", dataIndex: "description", width: "300px"},
                        {title: "创建时间", align: "center", dataIndex: "createTime"},
                        {title: "操作", align: "center", dataIndex: "operation", btns: ["update"]},
                    ],
                    list: [],
                },
                relationTable: {
                    loading: false,
                    pagination: false,
                    columns: [
                        {title: "序号", align: "center", dataIndex: "sque"},
                        {title: "集群名称", align: "center", dataIndex: "cluster"},
                        {title: "负责人", align: "center", dataIndex: "person"},
                        {title: "创建时间", align: "center", dataIndex: "createTime"},
                        {title: "操作", align: "center", dataIndex: "operation", btns: ["update"]},
                    ],
                    list: [],
                }
            });

            const getColonyList = async () => { // 获取集群列表数据
                state.listTable.loading = true;
                 let timer = setTimeout(() => {
                    state.listTable.loading = false;
                }, store.loadingTime);
                const data:any = await getColonyData({});
                state.listTable.loading = false;
                if(!data.data) {
                    message.warning(data.message || state.apiInfoMsg);
                    return;
                }
                state.listTable.list = data.data || [];
            }

            const getColonyRelationList = async () => { // 获取集群关联列表数据
                state.relationTable.loading = true;
                let timer = setTimeout(() => {
                    state.relationTable.loading = false;
                }, store.loadingTime);
                const data:any = await getColonyRelationData({});
                state.relationTable.loading = false;
                if(!data.data) {
                    message.warning(data.message || state.apiInfoMsg);
                    return;
                }
                let list:any = [];
                for(let i = 0; i < data.data.length; i++) {
                    list.push(Object.assign({person: "", userIds: []}, data.data[i].clusterVo2));
                    for(let j = 0; j < data.data[i].userVoList.length; j++) {
                        list[i].person += 
                            data.data[i].userVoList[j].username + 
                            (j == data.data[i].userVoList.length - 1 ? "" : ",");

                        list[i].userIds.push(data.data[i].userVoList[j].userId);
                    }
                }
                state.relationTable.list = list;
            }

            getColonyList();

            getColonyRelationList();

            const addColony = () => { // 集群新增按钮
                (state.addColonyModalRef as any).open();
            }

            const addColonyRelation = () => { // 集群关联新增按钮
                (state.addRelationModalRef as any).open();
            }

            const colonyUpdate = (row:any) => { // 集群修改
                (state.addColonyModalRef as any).open(row);
            }

            const relationUpdate = (row:any) => { // 集群关联修改
                (state.addRelationModalRef as any).open(row);
            }

            const addColonySave = async (val:any, type:any, callback:any) => { // 新增集群保存
                let timer = setTimeout(() => {
                    callback();
                }, store.loadingTime);
                let data:any = {};
                if(type == 0) {
                    data = await addColonyData(val)
                } else if(type == 1) {
                    data = await updateColonyData(val)
                }
                callback();
                if(data.message == "请求成功") {
                    message.success((type == 0 ? "新增" : "修改") + "成功");
                    getColonyList();
                    (state.addColonyModalRef as any).close()
                } else {
                    message.warn(data.message || state.apiInfoMsg)
                }
            }

            const addColonyRelationSave = async (val:any, type:any, callback:any) => { // 新增集群关联保存
                let timer = setTimeout(() => {
                    callback();
                }, store.loadingTime);
                let data:any = await relationColonyData(val);
                callback()
                if(data.message == "请求成功") {
                    message.success("关联成功");
                    getColonyRelationList();
                    (state.addRelationModalRef as any).close();
                } else {
                    message.warn(data.message || state.apiInfoMsg)
                }
            }

            const relationTableChange = (pagination:any) => { // 集群管理列表分页
                // state.relationTable.pagination.pageSize = pagination.pageSize;
                // state.relationTable.pagination.current = pagination.current
            }

            return {
                ...toRefs(state),
                relationTableChange,
                addColony,
                addColonyRelation,
                addColonySave,
                addColonyRelationSave,
                colonyUpdate,
                relationUpdate
            }
        }
    })
</script>

<style lang="less" scoped>
    .admin-page {
        .view-relation {
            margin-top: 20px;
        }
        .view-colony {
            border-radius: 5px;
            .view-title {
                p {
                    margin: 0;
                    font-size: 16px;
                    font-weight: bold;
                }
            }
            .view-title {
                display: flex;
                justify-content: space-between;
                .view-title-btn {
                    span {
                        white-space: nowrap;
                        color: #00B173;
                        cursor: pointer;
                    }
                }
            }
            .view-table {
                margin-top: 15px;
                padding: 15px 20px;
                background-color: #fff;
                .msg-table-text {
                    color: #00B173;
                    cursor: pointer;
                    &:last-child {
                        margin-left: 15px;
                    }
                }
            }
        }
    }
</style>
