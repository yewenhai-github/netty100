import router from '@/router';
import { RouteLocationNormalized } from 'vue-router';
import {message} from 'ant-design-vue';

const getUser = () => {
    return window.sessionStorage.user ? JSON.parse(window.sessionStorage.user) : null;
}

// 路由白名单
const whiteList:any = ["/login"];

// const store = useAppStore();

// 路由拦截
router.beforeEach((to:any, from:RouteLocationNormalized, next:any) => {
    // 白名单直接跳转
    if(whiteList.includes(to.path)) {
        next()
    } else if(!getUser()) {
        router.push({path: "/login"});
        return;
    }
    if(to.meta.allows && !to.meta.allows.includes(getUser().selfUserType)) {
        message.error("您没有对应的权限");
        router.push({path: "/colony"});
        return;
    }
    next();
});