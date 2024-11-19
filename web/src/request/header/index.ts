import { get, post } from "@/request/http/http"

export const updateUserPwd = (data:any) => post("/app/user/password", data);// 修改密码
export const updateUserInfo = (data:any) => post("/app/user/update", data);// 修改用户信息