import { get, post } from "@/request/http/http"

export const getData = (data:any) => post("/app/message/page", data);// 获取数据