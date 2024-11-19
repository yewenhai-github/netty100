<template>
    <a-dropdown :trigger="['click']">
        <span class="ant-dropdown-link header-author">
            <img src="" alt="">
        </span>
        <template #overlay>
            <a-menu>
                <a-sub-menu key="sub1" title="系统管理">
                    <a-menu-item v-for="(item, i) of menuList" :key="i" @click="switchRouter(item)">{{item.label}}</a-menu-item>
                </a-sub-menu>
            </a-menu>
        </template>
    </a-dropdown>
</template>

<script lang="ts">
    import { defineComponent, reactive, toRefs } from "vue";
    export default defineComponent ({
        props: {
            menuList: {
                type: Array,
                default: () => []
            }
        },
        emits: ["switchRouter"],
        setup(props, contex) {
            const state = reactive({
            });

            const switchRouter = (item:any) => {
                contex.emit("switchRouter", item);
            }

            return {
                ...toRefs(state),
                switchRouter
            }
        }
    })
</script>

<style lang="less" scoped>
    .header-author {
        display: block;
        cursor: pointer;
        width: 40px;
        height: 40px;
        border-radius: 50%;
        line-height: 40px;
        background-color: #00B173;
        overflow: hidden;
    }
</style>
