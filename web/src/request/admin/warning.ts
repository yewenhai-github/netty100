import { get, post } from "@/request/http/http"


export const getData = (data:any) => post("/admin/config/list/" + data.serverId, data);// 获取数据
export const getThresholdData = (data:any) => post("/admin/config/type/list", data); // 获取阈值名称列表
export const addData = (data:any) => post("/admin/config/add", data); // 新增一条数据
export const delCurrData = (data:any) => post("/admin/config/delete/" + data.id, data); // 删除数据
export const updateCurrData = (data:any) => post("/admin/config/update", data);// 修改数据