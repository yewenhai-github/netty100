import { get, post, del } from "@/request/http/http"


export const getData = (data:any) => post("/admin/device/query/page", data);// 获取数据
export const delData = (data:any) => del("/admin/device/delete/" + data.id, data);// 删除
export const addData = (data:any) => post("/api/nameserver/client/registration-code", data);// 新增
export const updateData = (data:any) => post("/admin/device/update", data);// 修改