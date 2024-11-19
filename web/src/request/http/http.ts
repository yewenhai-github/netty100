import axios from "axios";
import {baseUrl} from "../config";
// import router from '@/router';
import {useRouter} from "vue-router";
import {message} from 'ant-design-vue';

const whiteList = ["/app/user/login"];

const router = useRouter();

// const router:any = useRouter();

// 获取token
function getUserToken() {
    const token = window.sessionStorage.token;
    if(token) return window.sessionStorage.token;
    message.warning("当前用户已过期，请重新登录");
    const timer = setTimeout(() => {
        router.push({path: "/login"});
        clearTimeout(timer)
    }, 1500)
    // else router.push({path: "/login"});
}

// 设置请求头
function setRequestConfig(config: any) {
    const headers = {
        // 设置请求头
        "Content-type": "application/json;charset=utf-8",
        "Authorization": !whiteList.includes(config.url) && getUserToken()
    }
    config.url = baseUrl + config.url;
    // 文件下载
    if(config.params && config.params.httpType && config.params.httpType === "download") {
        config["responseType"] = "blob";
    }

    return headers;
}

// 下载文件
// function downloadFile(response: any) {
//     let bolb = new Blob([response.data], {type: "application/octet-stream"});
//     let filename = decodeURIComponent(response.headers["content-type"].split(";")[1].split("=")[1]);
//     if(window.navigator && window.navigator.msSaveOrOpenBlob) {
//         window.navigator.msSaveOrOpenBlob(blob, filename);
//     }
//     let a = document.createElement("a");
//     let url = window.URL.createObjectURL(Blob);
//     a.download = filename;
//     a.style.display = "none";
//     a.href = url;
//     document.body.appendChild(a);
//     a.click();
//     document.body.removeChild(a);
//     window.URL.removeObjectURL(url);
// }

// 超时时间
// axios.defaults.timeout = 120 * 1000;
// axios.defaults.baseURL = "";

// 请求拦截器
axios.interceptors.request.use(
    config => {
        config.data = JSON.stringify(config.data);
        config.headers = setRequestConfig(config);
        if(config.method === "get") {
            config.data = true;
        }
        
        return config;
    },
    error => Promise.reject(error)
);

// 响应拦截器
axios.interceptors.response.use(
    response => {
        if(response.data.message == "token无效,请重新登录") {
            sessionStorage.clear();
            router.push("/login");
            return;
        }
        switch(response.status) {
            case 200:
                return Promise.resolve(response.data);
            default:
                return Promise.reject(response.data);
        }
    },
    error => {
        switch(error && error.response && error.response.status) {
            case 401:
                message.warning(error.response.data.message || "对不起，您没有对应的权限");
                break;
            case 412:
                message.warning(error.response.data.message || "请求异常，请稍后重试");
                break;
            default:
                message.warning(error.response.data.message)
        }
        return Promise.reject(error);
    }
);

// get
export function get(url: string, params: any = {}) {
    return new Promise((resolve, reject) => {
        axios.get(url, {params: params}).then(response => {
            resolve(response);
        }).catch(error => {
            reject(error)
        })
    })
}

// delete
export function del(url: string, params: any = {}) {
    return new Promise((resolve, reject) => {
        axios.delete(url, {params: params}).then(response => {
            resolve(response);
        }).catch(error => {
            reject(error)
        })
    })
}

// post
export function post(url: string, data: any = {}) {
    return new Promise((resolve, reject) => {
        axios.post(url, data).then(response => {
            resolve(response);
        },
        error => {
            reject(error)
        })
    })
}