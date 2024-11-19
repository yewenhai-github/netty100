import { get, post } from "@/request/http/http"

export const getData = (data:any) => post("/app/kernel/message/query/page", data);// 获取数据