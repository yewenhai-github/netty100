<template>
    <div class="bg">
        <div class="fontend_container">
            <div class="view">
                <div class="view-title">
                    <PageTitle name="疑难杂症" />
                </div>
                <div class="view-detail">
                    <div class="view-detail-search">
                        <a-input-group compact>
                            <a-input v-model:value="form.title" style="width: calc(100% - 65px)" placeholder="请填写问题名称"/>
                            <a-button type="primary" @click="getList">搜索</a-button>
                        </a-input-group>
                    </div>
                    <a-collapse v-model:activeKey="activeKey" :bordered="false" accordion>
                        <a-collapse-panel v-for="(li, i) of table.list" :key="i" :header="li.title">
                            <div class="ql-editor" v-html="li.content"></div>
                        </a-collapse-panel>
                    </a-collapse>
                </div>
            </div>
        </div>
    </div>
</template>

<script lang="ts">
    import {defineComponent, reactive, toRefs, createVNode, nextTick} from "vue";
    import PageTitle from "@/components/PageTitle/PageTitle.vue";
    import {getData, delData, addData, updateData} from "@/request/admin/helpMore";
    import {message, Modal} from 'ant-design-vue';
    import {useAppStore} from "@/store/index";


    export default defineComponent ({
        components: {
            PageTitle
        },
        setup() {

            const store = useAppStore();

            const state = reactive({
                apiInfoMsg: "api接口响应异常，请重试",
                searchModal: "",
                activeKey: "1",
                text: "",
                importanceList: [
                    {value: "", label: "全部"},
                    {value: "1", label: "低"},
                    {value: "2", label: "中"},
                    {value: "3", label: "高"}
                ],
                form: {
                    title: "",
                    importance: "",
                },
                table: {
                    loading: false,
                    pagination: {
                        current: 1,
                        pageSize: 1000,
                        total: 0
                    },
                    list: []
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

            return {
                ...toRefs(state),
                getList,
            }
        }
    })
</script>

<style>
    .ant-collapse-borderless {
        border-radius: 5px;
        background-color: #ffffff;
    }
</style>

<style lang="less" scoped>
     .bg {
        .view {
            padding: 15px 20px;
            .view-detail {
                background-color: #ffffff;
                padding: 20px;
                margin-top: 15px;
                .view-detail-search {
                    margin-bottom: 20px;
                }
            }
        }
     }
</style>
