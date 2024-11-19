import { get, post } from "@/request/http/http"

export const getColonyData = (data:any) => post("/admin/cluster/info", data);// 获取集群简略数据
export const getColonyRelationData = (data:any) => post("/admin/cluster/user-cluster/list", data);// 获取集群关联数据
export const addColonyData = (data:any) => post("/admin/cluster/add", data);// 新增集群
export const relationColonyData = (data:any) => post("/admin/cluster/assign", data);// 用户关联集群
export const updateColonyData = (data:any) => post("/admin/cluster/update", data);// 修改集群信息