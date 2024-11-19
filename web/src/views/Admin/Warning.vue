<template>
    <div class="admin-page">
        <div class="war">
            <div class="war-title">
                <PageTitle name="流量预警阈值配置" />
            </div>
            <div class="war-table view-item">
                <div class="war-table-btns">
                    <div class="war-table-btns-search">
                        <a-form layout="inline" :label-col="{style: {width: '70px'}}" :wrapperCol="{span: 16}">
                            <a-form-item label="集群">
                                <a-select
                                    style="width: 300px;"
                                    ref="select"
                                    v-model:value="colonySelectModel"
                                    @change="colonySelectChange">
                                    <a-select-option v-for="item of colonySelectList" :value="item.id" :key="item.id">{{item.cluster}}</a-select-option>
                                </a-select>
                            </a-form-item>
                            <a-form-item label="节点">
                                <a-select
                                    style="width: 300px;"
                                    ref="select"
                                    v-model:value="nodeSelectModel">
                                    <a-select-option v-for="item of nodeSelectList" :value="item.id" :key="item.id">{{item.intranetIp}}</a-select-option>
                                </a-select>
                            </a-form-item>
                            <a-form-item style="width: 300px;">
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
                    <div class="war-table-btns-btn" @click="add">
                        <span>
                            <PlusOutlined/>
                            新增
                        </span>
                    </div>
                </div>
                <div class="war-table-list">
                    <a-tabs v-model:activeKey="tabsModel" @change="tabsChange">
                        <a-tab-pane key="client" tab="App端"></a-tab-pane>
                        <a-tab-pane key="server" tab="服务器"></a-tab-pane>
                        <a-tab-pane key="kernel" tab="内核"></a-tab-pane>
                    </a-tabs>
                    <Table 
                        :pagination="table.pagination"
                        :columns="table.columns"
                        :loading="table.loading"
                        :dataList="table.list" 
                        @update="update"
                        @del="del"/>
                </div>
            </div>
        </div>
        <div class="modal">
            <a-modal title="新增/修改" width="30%" :maskClosable="false" v-model:visible="modal.state">
                <template #footer>
                    <a-button key="back" @click="modal.state = !modal.state">取消</a-button>
                    <a-button key="submit" type="primary" :loading="modal.saveLoading" @click="save">保存</a-button>
                </template>
                <a-form :model="modal.form" :label-col="{style: {width: '100px'}}" :wrapperCol="{span: 16}">
                    <a-form-item label="分组名称">
                        <a-select
                            ref="select"
                            :disabled="modal.type == 1"
                            v-model:value="modal.form.typeGroup">
                            <a-select-option v-for="item in groupList" :value="item.value" :key="item.value">{{item.label}}</a-select-option>
                        </a-select>
                    </a-form-item>
                    <a-form-item label="阈值名称">
                        <a-select
                            ref="select"
                            :disabled="modal.type == 1"
                            v-model:value="modal.form.typeIndex">
                            <a-select-option v-for="item of thresholdList" :value="item.index" :key="item.index">{{item.title}}</a-select-option>
                        </a-select>
                    </a-form-item>
                    <a-form-item label="阈值">
                        <a-input v-model:value="modal.form.typeThreshold" />
                    </a-form-item>
                </a-form>
            </a-modal>
        </div>
    </div>
</template>

<script lang="ts">
    import {defineComponent, reactive, toRefs, computed, createVNode} from "vue";
    import {DeleteOutlined, PlusOutlined, ExclamationCircleOutlined} from "@ant-design/icons-vue";
    import {getData, getThresholdData, addData, delCurrData, updateCurrData} from "@/request/admin/warning";
    import {getColonyBrief, getNodeBriefList} from "@/request/colony/index";
    import {message, Modal} from 'ant-design-vue';
    import {clone} from "@/assets/js/common.js";
    import {useAppStore} from "@/store/index";
    import PageTitle from "@/components/PageTitle/PageTitle.vue";


    export default defineComponent ({
        components: {
            DeleteOutlined,
            PlusOutlined,
            PageTitle
        },
        setup() {

            const store = useAppStore();

            const state = reactive({
                apiInfoMsg: "api接口响应异常，请重试",
                tabsModel: "client",
                colonySelectList: [],
                colonySelectModel: "",
                nodeSelectList: [],
                nodeSelectModel: "",
                defaultColonySelectModel: "",
                defaultNodeSelectModel: "",
                thresholdSourceData: {},// 阈值源数据
                groupList: [
                    {label: "App端", value: "client"},
                    {label: "服务器", value: "server"},
                    {label: "内核", value: "kernel"}
                ],
                table: {
                    loading: false,
                    currRow: {id: 0},
                    pagination: false,
                    columns: [
                        {title: "序号", align: "center", dataIndex: "sque"},
                        {title: "分组名称", align: "center", dataIndex: "typeGroup"},
                        {title: "阈值名称", align: "center", dataIndex: "typeTitle"},
                        {title: "阈值", align: "center", dataIndex: "typeThreshold"},
                        {title: "操作", align: "center", dataIndex: "operation", btns: ["update", "del"]}
                    ],
                    sourceData: {},
                    list: [],
                },
                modal: {
                    state: false,
                    saveLoading: false,
                    type: 0, // 0 新增  1 修改
                    form: {
                        typeIndex: "",
                        typeGroup: "",
                        typeThreshold: "",
                    }
                }
            });

            const getList = async () => { // 获取表格内容
                state.table.loading = true;
                let timer = setTimeout(() => {
                    state.table.loading = false;
                }, store.loadingTime);
                const data:any = await getData({serverId: state.nodeSelectModel || 0});
                state.table.loading = false;
                if(!data.data) {
                    message.warning(data.message || state.apiInfoMsg);
                    return;
                }
                state.table.sourceData = data.data;
                tabsChange(state.tabsModel);
            }

            const getColonyList = async () => { // 获取集群列表
                const data:any = await getColonyBrief({});
                if(!data.data) {
                    message.warning(data.message || state.apiInfoMsg);
                    return;
                }
                state.colonySelectList = data.data;
                state.colonySelectModel = data.data[0].id;
                state.defaultColonySelectModel = data.data[0].id;
                getNodeList();
            }

            const getNodeList = async () => { // 获取节点列表
                const data:any = await getNodeBriefList({colonyId: state.colonySelectModel});
                if(!data.data) {
                    message.warning(data.message || state.apiInfoMsg);
                    return;
                }
                state.nodeSelectList = data.data;
                state.nodeSelectList.unshift({intranetIp: "请选择", id: ""});
            }

            const getThresholdList = async () => { // 获取阈值列表
                const data:any = await getThresholdData({});
                if(!data.data) {
                    message.warning(data.message || state.apiInfoMsg);
                    return;
                }
                state.thresholdSourceData = data.data;
            }

            const addWarning = async () => {
                let req = {
                    serverId: state.nodeSelectModel || 0, // 服务器ID
                    typeGroup: state.modal.form.typeGroup,// 分组名称 // App端 服务器 内核
                    typeIndex: state.modal.form.typeIndex, // 阈值index
                    typeThreshold: state.modal.form.typeThreshold, // 阈值
                    typeTitle: "" // 阈值名称
                }
                if(!/^[\d+(.\d+)]{0,10}?$/.test(req.typeThreshold)) {
                    message.warning("阈值请填写1-10位数字");
                    return;
                }
                for(let i = 0; i < thresholdList.value.length; i++) {
                    if(thresholdList.value[i].index == state.modal.form.typeIndex) {
                        req.typeTitle = thresholdList.value[i].title;
                        break;
                    }
                }
                const data:any = await addData(req);
                state.modal.saveLoading = false;
                if(data.message == "请求成功") {
                    message.success("新增成功");
                    state.modal.state = false;
                    getList();
                } else {
                    message.warning(data.message || state.apiInfoMsg);
                    return;
                }
            }

            getColonyList();

            getList();

            getThresholdList();

            const tabsChange = (val:any) => { // tabs页改变
                state.table.list = state.table.sourceData[val];
            }

            const colonySelectChange = (val:any) => { // 集群下拉框改变
                state.nodeSelectModel = "";
                getNodeList();
            }

            const add = () => { // 新增数据
                for(let key in state.modal.form) {
                    state.modal.form[key] = "";
                }
                state.modal.type = 0;
                state.modal.state = true;
            }

            const save = () => { // 保存数据
                if(!/^[0-9]{1,12}$/.test(state.modal.form.typeThreshold)) {
                    message.warning("请填写1-12位数字");
                    return;
                }
                state.modal.saveLoading = true;
                let timer = setTimeout(() => {
                    state.modal.saveLoading = false;
                }, 3000);
                if(state.modal.type == 0) addWarning();
                else if(state.modal.type == 1) updateRow();
            }

            const update = (row:any) => {// 点击update按钮
                state.table.currRow = row;
                state.modal.form = clone(row, {});
                state.modal.type = 1;
                state.modal.state = true;
            }

            const updateRow = async () => {// 修改一条数据
                let req = {
                    id: state.table.currRow.id,
	                typeThreshold: state.modal.form.typeThreshold
                };
                const data:any = await updateCurrData(req);
                state.modal.saveLoading = false;
                if(data.message == "请求成功") {
                    message.success("修改成功");
                    getList();
                    state.modal.state = false;
                }
            }

            const del = (row:any) => {// 删除一条数据
                if(row.serverId == 0) {
                    message.warning("默认配置项不能删除");
                    return;
                }
                Modal.confirm({
                    title: "提示",
                    icon: createVNode(ExclamationCircleOutlined),
                    content: "确定要删除当前行吗?",
                    okText: "确认",
                    cancelText: "取消",
                    onCancel: () => {
                        state.modal.state = false;
                    },
                    onOk: async () => {
                        const data:any = await delCurrData({id: row.id});
                        if(data.message == "请求成功") {
                            message.success("删除成功");
                            getList();
                        }
                    }
                });
            }

            const resetForm = () => {
                state.colonySelectModel = state.defaultColonySelectModel;
                state.nodeSelectModel = state.defaultNodeSelectModel;
                colonySelectChange(state.defaultColonySelectModel);
            }

            const thresholdList = computed(() => {
                let list = [];
                state.modal.type == 0 ? state.modal.form.typeIndex = "" : "";
                list = state.thresholdSourceData[state.modal.form.typeGroup];
                return list;
            });

            return {
                ...toRefs(state),
                save,
                tabsChange,
                colonySelectChange,
                thresholdList,
                update,
                getList,
                resetForm,
                del,
                add
            }
        }
    })
</script>

<style lang="less" scoped>
    .admin-page {
        .war {
            .view-item {
                background-color: #ffffff;
                border-radius: 5px;
                padding: 15px 20px;
            }
            .war-title {
                p {
                    margin: 0;
                    font-weight: bold;
                    font-size: 16px;
                }
            }
            .war-table {
                margin-top: 15px;
                .war-table-btns {
                    display: flex;
                    justify-content: space-between;
                    color: #00B173;
                    margin-bottom: 15px;
                    .war-table-btns-btn {
                        span {
                            white-space: nowrap;
                            cursor: pointer;
                        }
                    }
                }
            }
        }
    }
</style>
