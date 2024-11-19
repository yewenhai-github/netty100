<template>
    <div class="table" :style="'width:' + config.width + ';' + 'height:' + config.height">
        <ul>
            <li class="title">
                <div class="title-sque">序号</div>
                <div class="title-type">告警类型</div>
                <div class="title-time">告警时间</div>
                <div class="title-detail">详情</div>
                <!-- <div class="title-obj">告警对象</div> -->
            </li>
            <li class="dataItem" v-for="(item, i) of config.options.data" :key="i">
                <div class="title-sque">{{i + 1}}</div>
                <div class="title-type">{{item.shortName}}</div>
                <div class="title-time">{{item.createTime}}</div>
                <div class="title-detail">
                    <span @click="chartsTableClick(item)">查看</span>
                </div>
                <!-- <div class="title-obj">{{item.serverId}}</div> -->
            </li>
        </ul>
    </div>
</template>

<script lang="ts">
    import { defineComponent, reactive, toRefs, getCurrentInstance } from "vue";
    
    export default defineComponent ({
        components: {
        },
        props: {
            config: {
                type: Object,
                default: () => {}
            }
        },
        emits: ["chartsTableClick"],
        setup(props, contex) {

            const instance:any = getCurrentInstance();
            const $parent = instance.parent;
            
            const state = reactive({
                addWarningList: [
                    // {sque: 1, type: "告警类型1", time: "2022-04-21 13:36", obj: "张小鱼"},
                ]
            });

            const chartsTableClick = (item:any) => {
                $parent.emit("chartsTableClick", item);
            }

            return {
                ...toRefs(state),
                chartsTableClick
            }
        }
    })
</script>

<style lang="less" scoped>
    .table {
        overflow-y: auto;
        &::-webkit-scrollbar {
            width: 5px;
            height: 1px;
        }
        &::-webkit-scrollbar-thumb {
            border-radius: 10px;
            box-shadow: inset 0 0 5px rgba(0, 0, 0, 0.2);
            background: #989898;
        }
        &::-webkit-scrollbar-track {
            box-shadow: inset 0 0 5px rgba(0, 0, 0, 0.2);
            border-radius: 10px;
            background: #25255D;
        }
        ul {
            border-radius: 8px;
            overflow: hidden;
            li {
                display: flex;
                height: 43px;
                line-height: 43px;
                &:nth-child(even) {
                    background-color: #2B257A;
                }
                &:nth-child(odd) {
                    background-color: #1F2264;
                }
                div {
                    color: #989898;
                    font-size: 12px;
                    text-align: center;
                }
                .title-sque {
                    flex: 1;
                }
                .title-type {
                    flex: 2;
                }
                .title-time {
                    flex: 3;
                }
                .title-detail {
                    flex: 1;
                    span {
                        color: #0FE6FF;
                        cursor: pointer;
                        &:hover {
                            text-decoration: underline;
                        }
                    }
                }
                .title-obj {
                    flex: 2;
                }
            }
            .dataItem {
                div {
                    font-size: 14px;
                    color: #ffffff;
                }
            }
        }
    }

</style>
