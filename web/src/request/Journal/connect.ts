import { get, post } from "@/request/http/http"

export const getData = (data:any) => post("/app/client-channel/query", data);// 获取数据