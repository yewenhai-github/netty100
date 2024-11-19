<template>
    <div class="connect">
        <div class="connect-table">
            <Table 
                :pagination="pagination"
                :columns="columns"
                :loading="loading"
                :dataList="dataList"
                :scroll="{ x: 1200 }"
                @change="tableChange" />
        </div>
    </div>
</template>

<script lang="ts">
    import {defineComponent, reactive, toRefs} from "vue";
    import Table from "@/components/Table/Table.vue";

    export default defineComponent ({
        components: {
            Table
        },
        props: {
            pagination: {
                type: Object,
                default: () => {}
            },
            columns: {
                type: Object,
                default: () => {}
            },
            dataList: {
                type: Array,
                default: () => []
            },
            loading: {
                type: Boolean,
                default: false,
            }
        },
        emits: ["change"],
        setup(props, contex) {

            const state = reactive({
                
            });

            const tableChange = (pagination:any) => {
                contex.emit("change", pagination);
            }

            return {
                ...toRefs(state),
                tableChange
            }
        }
    })
</script>

<style lang="less" scoped>
    .connect-table {
        padding: 0px 20px;
        background-color: #ffffff;
        border-radius: 5px;
        overflow: hidden;
    }
</style>
