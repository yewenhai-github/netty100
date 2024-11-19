<template>
    <a-modal 
        title="新增/修改" 
        width="30%" 
        :maskClosable="false" 
        v-model:visible="modal.state">
        <template #footer>
            <a-button key="back" @click="modal.state = !modal.state">取消</a-button>
            <a-button key="submit" type="primary" :loading="modal.saveLoading" @click="save">保存</a-button>
        </template>
        <a-form 
            ref="formRef"
            :model="modal.form" 
            :label-col="{style: {width: '150px'}}" 
            :wrapperCol="{span: 16}">
            <a-form-item label="集群名称" name="cluster" :rules="modal.rules.cluster">
                <a-input v-model:value="modal.form.cluster" />
            </a-form-item>
            <a-form-item label="集群描述" name="description" :rules="modal.rules.description">
                <a-textarea show-count v-model:value="modal.form.description" :maxlength="255" />
            </a-form-item>
        </a-form>
    </a-modal>
</template>

<script lang="ts">
    import {defineComponent, reactive, toRefs} from "vue";
    import {DeleteOutlined, PlusOutlined} from "@ant-design/icons-vue";
    import {clone} from "@/assets/js/common.js";

    export default defineComponent ({
        components: {
            DeleteOutlined,
            PlusOutlined
        },

        emits: ["save"],

        setup(props, contex) {

            const state = reactive({
                formRef: null,
                modal: {
                    state: false,
                    saveLoading: false,
                    rules: {
                        cluster: [{required: true, pattern: new RegExp(/^[a-z]{1,25}$/), message: "集群名称只能为小写英文字母,长度1-20"}],
                        description: [{required: true, message: "请填写集群描述"}]
                    },
                    type: 0, // 0 新增   1 修改
                    sourceForm: {
                        cluster: "",
                        id: 0
                    },
                    form: {
                        cluster: "",
                        description: "",
                    }
                }
            })

            const save = () => {
                state.modal.saveLoading = true;
                let timer = setTimeout(() => {
                    state.modal.saveLoading = false;
                }, 15000);
                (state.formRef as any).validate().then(async (val:any) => {
                    // 回调函数用于清楚loading
                    let data = {
                        description: state.modal.form.description,
                        cluster: state.modal.form.cluster
                    };
                    if(state.modal.type == 1) {
                        data.id = state.modal.sourceForm.id;
                        if(state.modal.sourceForm.cluster == state.modal.form.cluster) { // 如果集群名称一样不传
                            delete data.cluster;
                        }
                    }
                    contex.emit("save", data, state.modal.type, () => {state.modal.saveLoading = false});
                }).catch(() => {
                    state.modal.saveLoading = false;
                })
            }

            const open = (row:any) => {
                for(let key in state.modal.form) {
                    state.modal.form[key] = "";
                }
                if(row) {
                    state.modal.sourceForm = row;
                    const formData = clone(row);
                    state.modal.form = formData;
                    state.modal.type = 1;
                } else {
                    state.modal.type = 0;
                }
                state.modal.state = true;
            }

            const close = () => {
                state.modal.state = false;
            }

            return {
                ...toRefs(state),
                save,
                open,
                close
            }
        }
    })
</script>