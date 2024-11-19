<template>
    <div>
        <a-table 
            ref="tableRef"
            :pagination="pageOptions" 
            @change="tableChange"
            :loading="loading"
            :row-key="(record) => record.id"
            :expandedRowKeys="expandedRowKeys"
            @expand="expand"
            :columns="columns" 
            :data-source="dataList" 
            :scroll="scroll"
            align="center">
            <template #bodyCell="{ column, text, index, record }">
                <a-tooltip v-if="column.width && column.dataIndex != 'sque' && column.type != 'status'">
                    <template #title>{{text}}</template>
                    <div :class="[column.className, 'text-overflow']" :style="'width:' + column?.width" @click.stop="cellClick(record, column)">
                        {{text}}
                    </div>
                </a-tooltip>
                <div v-if="column.dataIndex && !column.width && column.type != 'status'" :class="[column.className]">
                    {{text}}
                </div>
                <div v-if="column.dataIndex == 'sque'">
                    {{record.sque ? record.sque : pagination ? (pagination.current - 1) * pagination.pageSize + index + 1 : index + 1}}
                </div>
                <div v-if="column.type == 'status'" :className="record.table_className || ''">
                    {{text}}
                </div>
                <span v-if="column.dataIndex == 'operation' && column.btns">
                    <span v-show="column.btns.includes('update')" title="修改" class="msg-table-text" @click="update(record)">修改</span>
                    <a-tooltip placement="top">
                        <template #title>
                            <span>删除</span>
                        </template>
                        <span v-show="column.btns.includes('del')" title="删除" class="msg-table-text" @click="del(record)">
                            <DeleteOutlined />
                        </span>
                    </a-tooltip>
                </span>
            </template>
        </a-table>
    </div>
</template>

<script lang="ts">
    import { computed, defineComponent, onMounted, reactive, toRefs, getCurrentInstance } from "vue";
    import {DeleteOutlined} from "@ant-design/icons-vue";

    export default defineComponent ({
        props: {
            pagination: {
                type: [Object, Boolean]
            },
            columns: {
                type: Array,
                default: () => []
            },
            dataList: {
                type: Array,
                default: () => []
            },
            scroll: {
                type: Object,
                default: () => {}
            },
            loading: {
                type: Boolean,
                default: false
            }
        },
        emits: ["change", "update", "del", "cellClick"],
        components: {
            DeleteOutlined
        },
        setup(props, contex) {

            const state = reactive({
                tableRef: null,
                expandedRowKeys: [""]
            });

            const expand = (expanded:boolean, record:any) => {
                if (state.expandedRowKeys.length > 0) { //进这个判断说明当前已经有展开的了
                    //返回某个指定的字符串值在字符串中首次出现的位置，下标为0
                    let index = state.expandedRowKeys.indexOf(record.id);
                    if (index > -1) { //如果出现则截取这个id,1d到1相当于0，针对重复点击一个
                        state.expandedRowKeys.splice(index, 1);
                    } else {
                        //如果没出现则截取所有id,添加点击id，0到1，针对已经有一个展开，点另一个会进入判断
                        state.expandedRowKeys.splice(0, state.expandedRowKeys.length);
                        state.expandedRowKeys.push(record.id);
                    }
                } else {
                    //数组长度小于0，说明都没展开，第一次点击，id添加到数组，数组有谁的id谁就展开
                    state.expandedRowKeys.push(record.id);  
                }
            }

            const tableChange = (pagination:object) => {
                contex.emit("change", pagination)
            }

            const update = (row:any) => {
                contex.emit("update", row)
            }

            const del = (row:any) => {
                contex.emit("del", row)
            }
            
            const cellClick = (row:any, col:any) => {
                contex.emit("cellClick", row, col);
            }

            const getRef = () => {
                return state.tableRef
            }

            const pageOptions = computed(() => {
                return props.pagination && Object.assign(
                    {
                        pageSizeOptions: ['10', '20', '30'],
                        showTotal: (total:number, range:number[]) => {
                            return range[0] + '-' + range[1] + ' 共' + total + '条'
                        },
                        showQuickJumper: true,
                        showSizeChanger: true,
                    },
                    props.pagination
                )
            });

            onMounted(() => {
            });

            return {
                ...toRefs(state),
                tableChange,
                expand,
                update, 
                del,
                cellClick,
                getRef,
                pageOptions
            }
        }
    })
</script>

<style lang="less" scoped>
    .text_green {
        color: #00B173;
        cursor: pointer;
    }
    .text_red {
        color: #FF4D4F;
    }
    .text-overflow {
        display: inline-block;
        height: 22px;
        overflow: hidden;
        white-space: nowrap;
        text-overflow: ellipsis;
        position: relative;
        right: 16px
    }
    .msg-table-text {
        color: #00B173;
        cursor: pointer;
        &:last-child {
            margin-left: 15px;
        }
    }
</style>

<style lang="less">
    .ant-table-thead > tr > th {
        color: #868686;
        font-weight: bold;
        background-color: rgba(255, 255, 255);
        border-bottom: 1px solid #EBEEF5;
    }
    .ant-table-thead > tr > th:not(:last-child):not(.ant-table-selection-column):not(.ant-table-row-expand-icon-cell):not([colspan])::before {
        background-color: rgba(0, 0, 0, 0);
    }
    .ant-table-cell {
        overflow: hidden;
    }
    .ant-table-content {
        &::-webkit-scrollbar {
            width: 5px;
            height: 8px;
        }
        &::-webkit-scrollbar-thumb {
            border-radius: 10px;
            box-shadow: inset 0 0 5px rgba(0, 0, 0, 0.2);
            background: #C1C1C1;
        }
        &::-webkit-scrollbar-thumb:hover {
            background-color: #B2B2B2;
        }
        &::-webkit-scrollbar-track {
            box-shadow: inset 0 0 5px rgba(0, 0, 0, 0.2);
            border-radius: 10px;
            background: #ffffff;
        }
    }
    .ant-table-tbody .ant-table-row:not(.ant-table-row-level-0),
    .ant-table-tbody .ant-table-row:not(.ant-table-row-level-0) .ant-table-cell-fix-left {
        background-color: #f3f3f3;
    }
    .ant-tooltip {
        max-width: 1000px;
    }
</style>
