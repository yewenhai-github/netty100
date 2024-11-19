import App from './App.vue'
import pinia from "./store/index"
import { createApp } from 'vue'
import router from './router'
import Antd from 'ant-design-vue'
import "@/router/permission"
import {message} from 'ant-design-vue'
import loading from "@/directive/loading"
import '@vueup/vue-quill/dist/vue-quill.snow.css';

message.config({
    top: `80px`,
    duration: 2,
    maxCount: 3
});

const fontendApp = createApp(App);

// 屏蔽警告信息
fontendApp.config.warnHandler = () => null;

// 加载中...指令
fontendApp.directive("loading", loading);

fontendApp.use(pinia);
fontendApp.use(Antd);
fontendApp.use(router);
fontendApp.mount('#emp-root');


