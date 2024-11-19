import { get, post } from "@/request/http/http"

export const getData = (data:any) => post("/admin/protocol/page", data);// 获取数据
export const addMsg = (data:any) => post("/admin/protocol/add", data); // 新增消息
export const updateMsg = (data:any) => post("/admin/protocol/update", data); // 修改消息
export const delMsg = (data:any) => post("/admin/protocol/delete/" + data.id, data); // 删除消息
export const getMsgTypeList = (data:any) => post("/admin/protocol/type/list", data); // 获取消息列表
export const getAllSelectData = (data:any) => post("/admin/protocol/list", data);// 获取所有下拉框数据(type,way,source...)日志页面查询时候用到