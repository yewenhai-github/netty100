<template>
    <a-modal title="新增/修改" width="45%" :maskClosable="false" v-model:visible="modal.state">
        <template #footer>
            <a-button key="back" @click="modal.state = !modal.state">取消</a-button>
            <a-button key="submit" type="primary" :loading="modal.saveLoading" @click="save">保存</a-button>
        </template>
        <a-form ref="formRef" :model="modal.form" :label-col="{style: {width: '100px'}}" :wrapperCol="{span: 16}">
            <a-form-item label="项目名称" name="title" :rules="[{ required: true, message: '请填写项目名称'}]">
                <a-input v-model:value="modal.form.title" placeholder="请填写项目名称"/>
            </a-form-item>
            <a-form-item label="重要性">
                <a-select
                    ref="select"
                    v-model:value="modal.form.importance">
                    <a-select-option v-for="item of calcImportanceList" :value="item.value" :key="item.value">{{item.label}}</a-select-option>
                </a-select>
            </a-form-item>
            <div class="form-item-editor">
                <QuillEditor 
                    ref="qeRef"
                    height="500px" />
            </div>
        </a-form>
    </a-modal>
</template>

<script lang="ts">
    import {computed, defineComponent, nextTick, reactive, toRefs} from "vue";
    import QuillEditor from "@/components/QuillEditor/QuillEditor";
    import {clone} from "@/assets/js/common.js";

    export default defineComponent ({
        components: {
            QuillEditor
        },

        emits: ["save"],

        props: {
            importanceList: {
                type: Array,
                default: () => []
            }
        },

        setup(props, contex) {

            const state = reactive({
                formRef: null,
                qeRef: null,
                modal: {
                    state: false,
                    saveLoading: false,
                    type: 1, // 1修改  0新增
                    form: {
                        title: "",
                        importance: "1",
                        content: "",
                    }
                }
            });

            const calcImportanceList = computed(() => {
                let list:any = clone(props.importanceList, []);
                list.splice(0, 1);
                return list;
            });

            const clearModalForm = () => { // 清空modal中的form
                for(let key in state.modal.form) {
                    state.modal.form[key] = "";
                }
                state.modal.form.importance = "1";
            };

            const save = () => {
                (state.formRef as any).validate().then(async (val:any) => {
                    state.modal.saveLoading = true;
                    let timer = setTimeout(() => {
                        state.modal.saveLoading = false;
                    }, 3000);
                    let data = state.modal.form;
                    data.content = (state.qeRef as any).getHTML();
                    contex.emit("save", data, state.modal.type, () => {
                        state.modal.saveLoading = false;
                    });
                }).catch((err:any) => {})
            };

            const open = (row:any) => {
                clearModalForm();
                if(row) {
                    state.modal.type = 1;
                    state.modal.form = row;
                } else {
                    state.modal.type = 0;
                }
                state.modal.state = true;
                nextTick(() => {
                    (state.qeRef as any).setHTML(row ? row.content : "");
                });
            };

            const close = () => {
                state.modal.state = false;
            };

            return {
                ...toRefs(state),
                save,
                open,
                calcImportanceList,
                close
            }
        }
    })
</script>