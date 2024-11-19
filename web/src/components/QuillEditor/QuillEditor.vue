<template>
    <QuillEditor 
        theme="snow" 
        :style="'width:' + width + '; height:' + height"
        :options="options"
        ref="editorRef" 
        @blur="onEditorBlur($event)"
        @focus="onEditorFocus($event)"
        @change="onEditorChange($event)"
        @ready="onEditorReady($event)" />
</template>

<script lang="ts">
    import {computed, defineComponent, onMounted, reactive, toRefs} from "vue";
    import {QuillEditor} from '@vueup/vue-quill';
    // import '@vueup/vue-quill/dist/vue-quill.snow.css';
    
    export default defineComponent ({

        components: {
            QuillEditor
        },

        props: {
            width: {
                type: String,
                default: ""
            },
            height: {
                type: String,
                default: "300px"
            },
            config: {
                type: Object,
                default: () => {}
            }
        },
        
        setup(props, contex) {
            const state = reactive({
                editorRef: null,
                config: {
                    modules: {
                        toolbar: [
                            ['bold', 'italic', 'underline', 'strike'], // 加粗 斜体 下划线 删除线
                            ['blockquote', 'code-block'], // 引用  代码块
                            [{ header: 1 }, { header: 2 }], // 1、2 级标题
                            [{ list: 'ordered' }, { list: 'bullet' }], // 有序、无序列表
                            [{ script: 'sub' }, { script: 'super' }], // 上标/下标
                            [{ indent: '-1' }, { indent: '+1' }], // 缩进
                            [{ direction: 'rtl' }], // 文本方向
                            [{ size: ['small', false, 'large', 'huge'] }], // 字体大小
                            [{ header: [1, 2, 3, 4, 5, 6] }], // 标题
                            [{ color: [] }, { background: [] }], // 字体颜色、字体背景颜色
                            [{ align: [] }], // 对齐方式
                            ['clean'], // 清除文本格式
                            ['image'] // 链接、图片、视频
                        ]
                    },
                    placeholder: '请输入正文'
                }
            });

            const options:any = computed(() => {
                return props.config ? props.config : state.config;
            });

            // 失去焦点事件
            const onEditorBlur = (quill:any) => {
                // console.log('editor blur!', quill)
            }
            // 获得焦点事件
            const onEditorFocus = (quill:any) => {
                // console.log('editor focus!', quill)
            }
            // 准备富文本编辑器
            const onEditorReady = (quill:any) => {
                // console.log("ready", quill);
            }
            // 内容改变事件
            const onEditorChange = (res:any) => {
                // console.log('editor change!', res)
            }
            // 获取HTML
            const getHTML = () => {
                return (state.editorRef as any).getHTML();
            }
            // 设置HTML
            const setHTML = (html:string) => {
                (state.editorRef as any).setHTML(html);
            }

            return {
                ...toRefs(state),
                onEditorBlur,
                onEditorFocus,
                onEditorReady,
                onEditorChange,
                getHTML,
                setHTML,
                options
            }
        }
    })
</script>

<style lang="less" scoped>
</style>