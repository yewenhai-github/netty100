import { get, post } from "@/request/http/http"


export const getData = (data:any) => post("/admin/user/page", data);// 获取数据
export const addData = (data:any) => post("/admin/user/register", data);// 新增用户
export const delData = (data:any) => post("/admin/user/delete/" + data.id, data);// 删除用户
export const updateData = (data:any) => post("/admin/user/update", data);// 修改用户
export const getAllUserData = (data:any) => post("/admin/user/list", data);// 获取所有用户列表