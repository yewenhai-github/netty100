<template>
    <div class="chatbox">
        <ul>
            <li class="chat-box-item_other" v-for="item of chatList" :key="item">
                <div class="chat-item-name_time">
                    <p style="margin-left: 15px">{{item.time}}&nbsp;</p>
                    <p>{{item.name}}: </p>
                </div>
                <div class="chat-item-con">
                    {{item.content}}
                </div>
            </li>
            <!-- <li class="chat-box-item_self">
                <div class="chat-item-name_time">
                    <p>内核</p>
                    <p>14:59</p>
                </div>
                <div class="chat-item-con">
                    hello world
                </div>
            </li> -->
        </ul>
    </div>
</template>

<script lang="ts">
    import {computed, defineComponent, onMounted, reactive, toRefs, getCurrentInstance} from "vue";
    
    export default defineComponent ({

        components: {
        },

        props: {
            chatList: {
                type: Array,
                default: () => []
            }
        },
        
        setup(props, contex) {
            let currNode:any = null;
            const state = reactive({
               
            });
            const scrollBottom = () => {
                currNode.scrollTop = currNode.scrollHeight
            }
            onMounted(() => {
                currNode = getCurrentInstance()?.vnode.el;
            });
            return {
                ...toRefs(state),
                scrollBottom
            }
        }
    })
</script>

<style lang="less" scoped>
    .chatbox {
        width: 100%;
        min-height: 300px;
        max-height: 460px; 
        overflow-y: auto;
        border: 1px solid #d9d9d9;
        border-radius: 2px;
        padding: 15px 20px;
        p {
            display: inline-block;
            margin: 0;
        }
        ul {
            padding-bottom: 50px;
            &::after {
                content: "";
                display: block;
                clear: both;
            }
            li {
                display: flex;
                &:not(:first-child) {
                    margin-top: 15px;
                }
                .chat-item-name_time {
                    width: 21%;
                }
                .chat-item-con {
                    width: 77%;
                    white-space: wrap;
                    margin-left: 10px;
                }
            }
            // .chat-box-item_other {
            //     .chat-item-con {
            //         margin-left: 30px;
            //     }
            // }
            // .chat-box-item_self {
                // float: right;
                // text-align: right;
                // .chat-item-con {
                //     margin-right: 30px;
                // }
            // }
        }
    }
</style>