import { get, post } from "../http/http"

export const getColonyCluster = (data:any) => post("/app/cluster/list", data);// 获取集群详细信息

export const getColonyBrief = (data:any) => post("/app/cluster/brief/list", data);// 获取集群简略信息

export const getNodeList = (data:any) => post("/app/node/page", data);// 获取集群对应的节点列表

export const getNodeBriefList = (data:any) => post("/app/node/list/" + data.colonyId, data)

export const getClientTableList = (data:any) => post("/app/node/conn/client/page", data);// 获取客户端列表

export const getServeTableList = (data:any) => post("/app/node/conn/server/page", data); // 获取服务器列表