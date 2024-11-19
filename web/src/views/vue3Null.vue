<template>
    <div class="bg">
        <div class="fontend_container">
            <div class="view">
                this is null
            </div>
        </div>
    </div>
</template>

<script lang="ts">
    import { defineComponent, reactive, toRefs } from "vue";
    export default defineComponent ({
        components: {
        },
        setup() {

            const state = reactive({
                
            });

            return {
                ...toRefs(state)
            }
        }
    })
</script>

<style lang="less" scoped>
     .bg {
        background-color: #EEF4FC;
        padding: 50px 0px;
        .view {
            background-color: #ffffff;
            border-radius: 5px;
            padding: 15px 20px;
        }
     }
</style>
