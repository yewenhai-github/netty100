import { defineStore } from 'pinia'
import piniaStore from '@/store'
import { AppState } from './types';

const menu = {
    // , children: [
    //     {label: "连接日志", value: "connect", address: "/journal/connect"},
    //     {label: "消息日志", value: "message", address: "/journal/message"}
    // ]
    common: [
        {label: "集群", value: "colony", address: "/colony", userType: "common"},
        {label: "日志", value: "journal", address: "/journal/message", userType: "common", children: [
            {label: "连接日志", value: "connect", address: "/journal/connect"},
            {label: "消息日志", value: "message", address: "/journal/message"},
            {label: "内核日志", value: "kernel", address: "/journal/kernelJournal"}
        ]},
        {label: "监控", value: "warning", address: "/warning", userType: "common"},
        {label: "统计", value: "statistics", address: "/statistics", userType: "common"},
        {label: "帮助", value: "help", address: "/help", userType: "common", children: [
            {label: "客户端接入", value: "sdk", address: "/help/clientAccess"},
            {label: "服务器接入", value: "sdk", address: "/help/serveAccess"},
            {label: "操作手册", value: "manual", address: "/help/manual"},
            {label: "疑难杂症", value: "more", address: "/help/more"}
            // {label: "在线调试", value: "debugger", address: "/help/onlineTools"}
        ]},
        {label: "", value: "user", address: "", userType: "admin", children: [
            {label: "系统管理", value: "admin", userType: "admin", address: "/admin/msgAgreement"},
            {label: "修改密码", value: "updatePwd"},
            {label: "修改信息", value: "updateInfo"},
            {label: "退出", value: "quit"}
        ]}
    ],
    admin: [
        {label: "后台管理", value: "admin", address: "/admin", userType: "admin", 
            children: [
                {label: "首页", value: "index", address: "/colony", icon: "HomeOutlined"},
                {label: "集群管理", value: "colony", address: "/admin/colony", icon: "CloudServerOutlined"},
                {label: "消息协议", value: "project", address: "/admin/appAgreement", icon: "ProfileOutlined"},
                {label: "设备注册", value: "device", address: "/admin/deviceRegisterCode", icon: "DesktopOutlined"},
                {label: "疑难杂症", value: "helpMore", address: "/admin/helpMore", icon: "ReadOutlined"},
                {label: "告警设置", value: "warning", address: "/admin/warning", icon: "BellOutlined"},
                {label: "基础数据", value: "message", address: "/admin/msgAgreement", icon: "SoundOutlined"},
                {label: "用户管理", value: "user", address: "/admin/user", icon: "UserOutlined"}
            ]
        }
    ]
};

const loadingTime = 3000;

export const useAppStore = defineStore(
    // 唯一ID
    'app',
    {
        state: () => ({
            // title: "FastVue3,一个快速开箱即用的Vue3+Vite模板",
            // h1: 'Vue3+Vite2.x+Ts+Pinia大厂开发必备',
            // theme: 'dark',
            menu,
            loadingTime
        }),
        getters: {
        },
        actions: {
            // Update app settings
            // updateSettings(partial: Partial<AppState>) {
            //     this.$patch(partial);
            // },

            // Change theme color
            // toggleTheme(dark: boolean) {
            //     if (dark) {
            //         this.theme = 'dark';
            //         document.documentElement.classList.add('dark');
            //         document.body.setAttribute('arco-theme', 'dark');
            //         localStorage.setItem('theme',this.theme)
            //     } else {
            //         this.theme = 'light';
            //         document.documentElement.classList.remove('dark');
            //         document.body.removeAttribute('arco-theme');
            //         localStorage.setItem('theme',this.theme)
            //     }
            // }
        }
    }
)

export function useAppOutsideStore() {
    return useAppStore(piniaStore)
}