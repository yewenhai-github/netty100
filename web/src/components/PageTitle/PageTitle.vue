<template>
    <div class="view-title-text">
        <span></span>
        <p>{{name}}</p>
    </div>
</template>

<script lang="ts">
    import { defineComponent, onMounted, reactive, toRefs } from "vue";
    
    export default defineComponent ({

        props: {
            name: {
                type: String,
                default: ""
            }
        },
        setup() {

           
        }
    })
</script>

<style lang="less" scoped>
    .view-title-text {
        display: flex;
        align-items: center;
        span {
            display: inline-block;
            width: 5px;
            height: 19px;
            background: linear-gradient(90deg, #32B0FF 0%, #747BFC 100%);
            border-radius: 3px;
        }
        p {
            margin: 0px 0px 0px 10px;
            font-weight: bold;
            font-size: 20px;
            color: #333333;
        }
    }
</style>