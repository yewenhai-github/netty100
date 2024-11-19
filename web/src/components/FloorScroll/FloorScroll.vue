<template>
    <div class="fixed-nav">
        <FloorScrollChild class="fixed-nav-child" :navList="navList"/>
    </div>
</template>

<script lang="ts">
    import {defineComponent, reactive, toRefs} from "vue";
    import FloorScrollChild from "./FloorScrollChild.vue";

    export default defineComponent ({
        components: {
            FloorScrollChild
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

            return {
                ...toRefs(state),
            }
        }
    })
</script>

<style lang="less" scoped>
    .fixed-nav {
        background-color: #ffffff;
        border-radius: 3px;
        padding: 8px 15px;
        position: fixed;
        bottom: 10%;
        right: 5%;
    }
</style>