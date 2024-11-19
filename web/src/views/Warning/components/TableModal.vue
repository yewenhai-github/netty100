<template>
    <a-modal class="view-dialog-modal" v-model:visible="tableModal.state" :footer="{}" :maskClosable="false">
        <p v-html="currRow.detail"></p>
    </a-modal>
</template>

<script lang="ts">
    import { defineComponent, reactive, toRefs } from "vue";

    export default defineComponent({
        setup() {
            const state = reactive({
                currRow: null,
                tableModal: {
                    state: false
                }
            });

            const open = (val:any) => {
                state.currRow = val;
                state.tableModal.state = true;
            }

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
