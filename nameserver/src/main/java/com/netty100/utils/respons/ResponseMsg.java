package com.netty100.utils.respons;

/**
 * Controller响应信息
 *
 * @author rzcllove
 */

public interface ResponseMsg {

    /**
     * 用户相关
     */
    String USER_NOT_EXIST = "找不到指定的用户";

    String USERNAME_ALREADY_EXISTS = "账户名称已经存在";

    String NOT_LOGGED_IN = "用户尚未登录,请登录";

    String JWT_CREAT_ERROR = "生成token异常";

    String INVALID_TOKEN = "token无效,请重新登录";

    String INCORRECT_USERNAME_PASSWORD = "账户或者密码不正确";

    String INVALID_EMAIL_DING_TALK = "当接收告警信息时,邮箱和钉钉手机号码不能同时为空";

    String ACCEPT_WARN_STATUS = "只有管理员才能接收告警信息";

    /**
     * 集群相关
     */
    String CLUSTER_NOT_EXIST = "集群不存在";

    String CLUSTER_ALREADY_EXIST = "集群名称已经存在";

    String NOT_HAVE_CLUSTER = "该用户尚未分配负责集群";

    /**
     * 节点相关
     */
    String SERVER_ALREADY_EXISTS = "server节点已经存在";

    String SERVER_NOT_EXISTS = "server节点不存在";


    /**
     * token相关
     */
    String LIMITED_TOKEN = "错误的访问路径,权限不足";


    /**
     * 消息协议相关
     */
    String PROTOCOL_VALUE_EXIST = "协议项已经存在";

    /**
     * 告警阈值相关
     */
    String DEFAULT_CONFIG_DELETE = "默认配置不能删除";

    String CONFIG_ALREADY_EXISTS = "该配置项已经存在";

}
