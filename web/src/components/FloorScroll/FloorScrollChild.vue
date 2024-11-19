<template>
    <ul>
        <li v-for="(li, i) of navList" :key="i">
            <span @click.stop="hanldClick(li.id)">
                {{li.name}}
            </span>
            <FloorScrollChild class="fixed-nav-child" v-if="li.child" :navList="li.child"/>
        </li>
    </ul>
</template>

<script lang="ts">
    import {defineComponent, reactive, toRefs} from "vue";
    import {scrollElement} from "@/assets/js/common.js";
    // import {CaretDownOutlined} from "@ant-design/icons-vue";

    export default defineComponent ({
        name: "FloorScrollChild",
        components: {
            // CaretDownOutlined
        },
        props: {
            navList: {
                type: Object,
                default: () => {}
            }
        },
        setup(props, contex) {
            const state = reactive({
            });

            const hanldClick = (id:string) => {
                scrollElement(id);
            }

            return {
                ...toRefs(state),
                hanldClick
            }
        }
    })
</script>

<style lang="less" scoped>
    .fixed-nav {
        border-radius: 3px;
        padding: 8px 15px;
        ul {
            margin: 0;
            li {
                &:not(:first-child) {
                    margin-top: 10px;
                }
                span {
                    cursor: pointer;
                    color: #00B173;
                    &:hover {
                        text-decoration: underline;
                    }
                }
                .fixed-nav-child {
                    margin-left: 30px;
                }
            }
        }
    }
</style>