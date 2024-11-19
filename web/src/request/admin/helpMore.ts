import { get, post, del } from "@/request/http/http"


export const getData = (data:any) => post("/admin/issue/query/page", data);// 获取数据
export const delData = (data:any) => del("/admin/issue/remove/" + data.id, data);// 删除用户
export const addData = (data:any) => post("/admin/issue/add", data);// 新增用户
export const updateData = (data:any) => post("/admin/issue/update", data);// 修改用户