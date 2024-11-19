<template>
    <div class="admin-page">
        <div class="pro">
            <div class="pro-title">
                <PageTitle name="疑难杂症管理" />
            </div>
            <div class="pro-table view-item">
                <div class="pro-table-btns">
                    <div class="pro-table-btns-search">
                        <a-form layout="inline" :label-col="{style: {width: '70px'}}" :wrapperCol="{span: 16}">
                            <a-form-item label="标题">
                                <a-input style="width: 300px;" v-model:value="form.title" />
                            </a-form-item>
                            <a-form-item label="重要性">
                                <a-select
                                    style="width: 300px;"
                                    ref="select"
                                    v-model:value="form.importance">
                                    <a-select-option v-for="item of importanceList" :value="item.value" :key="item.value">{{item.label}}</a-select-option>
                                </a-select>
                            </a-form-item>
                            <a-form-item style="width: 300px">
                                <a-button type="primary" @click="getList">
                                    <template #icon><SearchOutlined /></template>
                                    搜索
                                </a-button>
                                <a-button style="margin-left: 15px" @click="resetForm">
                                    <template #icon><RedoOutlined /></template>
                                    重置
                                </a-button>
                            </a-form-item>
                        </a-form>
                    </div>
                    <div class="pro-table-btns-btn" @click="add">
                        <span>
                            <PlusOutlined/>
                            新增
                        </span>
                    </div>
                </div>
                <Table 
                    :pagination="table.pagination"
                    :columns="table.columns"
                    :loading="table.loading"
                    :dataList="table.list"
                    @change="tableChange"
                    @del="del"
                    @update="update" />
            </div>
        </div>
        <div class="modal">
            <HelpMoreAddModal @save="save" :importanceList="importanceList" ref="modalRef"/>
        </div>
    </div>
</template>

<script lang="ts">
    import {defineComponent, reactive, toRefs, createVNode} from "vue";
    import {PlusOutlined, ExclamationCircleOutlined} from "@ant-design/icons-vue";
    import {getData, delData, addData, updateData} from "@/request/admin/helpMore";
    import {message, Modal} from 'ant-design-vue';
    import {clone} from "@/assets/js/common.js";
    import {useAppStore} from "@/store/index";
    import PageTitle from "@/components/PageTitle/PageTitle.vue";
    import HelpMoreAddModal from "./components/HelpMoreAddModal.vue";

    export default defineComponent ({
        components: {
            PlusOutlined,
            PageTitle,
            HelpMoreAddModal
        },
        setup() {

            const store = useAppStore();

            const state = reactive({
                modalRef: null,
                apiInfoMsg: "api接口响应异常，请重试",
                colonySelectList: [] as any [],
                importanceList: [
                    {value: "", label: "全部"},
                    {value: "1", label: "低"},
                    {value: "2", label: "中"},
                    {value: "3", label: "高"}
                ],
                form: {
                    title: "",
                    cluster: "",
                    importance: "",
                },
                table: {
                    loading: false,
                    pagination: {
                        current: 1,
                        pageSize: 10,
                        pageSizeOptions: ['10', '20', '30'],
                        showTotal: (total:number, range:number[]) => {
                            return range[0] + '-' + range[1] + ' 共' + total + '条'
                        },
                        showQuickJumper: true,
                        showSizeChanger: true,
                        total: 0,
                    },
                    columns: [
                        {title: "序号", align: "center", dataIndex: "sque"},
                        {title: "标题", align: "center", dataIndex: "title"},
                        {title: "重要性", align: "center", dataIndex: "importanceDesc"},
                        {title: "操作", align: "center", dataIndex: "operation", btns: ["update", "del"]}
                    ],
                    list: [],
                }
            });

            const getList = async () => { // 获取表格数据
                state.table.loading = true;
                let req = {
                    // issueType: "",
                    title: state.form.title,
                    importance: state.form.importance,
                    pageNum: state.table.pagination.current,
                    pageSize: state.table.pagination.pageSize,
                }
                for(let key in req) {
                    if(!req[key]) delete req[key];
                }
                let timer = setTimeout(() => {
                    state.table.loading = false;
                }, store.loadingTime);
                const data:any = await getData(req);
                state.table.loading = false;
                 if(!data.data) {
                    message.warning(data.message || state.apiInfoMsg);
                    return;
                }
                
                for(let i = 0; i < data.data.list.length; i++) {
                    for(let j = 0; j < state.importanceList.length; j++) {
                        if(state.importanceList[j].value == data.data.list[i].importance) {
                            data.data.list[i].importanceDesc = state.importanceList[j].label;
                        }
                    }
                }
                state.table.list = data.data.list;
                state.table.pagination.total = data.data.total>>0;
            }

            getList();

            const add = () => { // 新增
                (state.modalRef as any).open();
            }

            const update = (row:any) => { // 点击修改按钮
                const data:any = clone(row);
                data.importance += "";
                (state.modalRef as any).open(data);
            }

            const delCurrRow = async (row:any) => {
                const data:any = await delData({id: row.id});
                if(data.data) {
                    message.success("删除成功");
                    getList();
                } else {
                    message.warn(data.message || state.apiInfoMsg)
                }
            }

            const del = (row:any) => { // 点击删除按钮
                Modal.confirm({
                    title: "提示",
                    icon: createVNode(ExclamationCircleOutlined),
                    content: "确定要删除当前行吗?",
                    okText: "确认",
                    cancelText: "取消",
                    onCancel: () => {
                    },
                    onOk: () => {
                        delCurrRow(row);
                    }
                });
            }

            const resetForm = () => {
                for(let key in state.form) {
                    state.form[key] = "";
                }
            }

            const tableChange = (pagination:any) => { // 分页改变
                state.table.pagination.pageSize = pagination.pageSize;
                state.table.pagination.current = pagination.current;
                getList();
            }

            const save = async (val:any, type:number, callback:any) => { // 保存
                let data:any = {};
                let timer = setTimeout(() => {
                    callback();
                }, store.loadingTime);
                if(type == 0) {
                    data = await addData(val);
                } else if(type == 1) {
                    data = await updateData(val);
                }
                callback();
                if(data.data) {
                    message.success((type == 0 ? "添加" : "修改") + "成功");
                    (state.modalRef as any).close();
                    getList();
                } else {
                    message.warn((type == 0 ? "添加" : "修改") + "失败");
                }
            }

            return {
                ...toRefs(state),
                getList,
                add,
                update,
                del,
                resetForm,
                tableChange,
                save
            }
        }
    })
</script>

<style lang="less" scoped>
    .admin-page {
        .pro {
            .view-item {
                background-color: #ffffff;
                border-radius: 5px;
                padding: 15px 20px;
            }
            .pro-title {
                p {
                    margin: 0;
                    font-weight: bold;
                    font-size: 16px;
                }
            }
            .pro-table {
                margin-top: 15px;
                .pro-table-btns {
                    display: flex;
                    justify-content: space-between;
                    color: #00B173;
                    margin-bottom: 15px;
                    .pro-table-btns-btn {
                        white-space: nowrap;
                        span {
                            cursor: pointer;
                        }
                    }
                }
            }
        }
    }
</style>
