import { get, post } from "../http/http"

export const login = (data:any) => post("/app/user/login", data);// 获取数据