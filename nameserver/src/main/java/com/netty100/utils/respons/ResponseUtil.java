package com.netty100.utils.respons;

import com.alibaba.fastjson.JSONObject;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author why
 */
@Slf4j
public class ResponseUtil {
    /**
     * 使用response输出JSON格式的响应结果
     *
     * @param response http response
     * @param obj      响应结果
     */
    public static void write(ServletResponse response, @NonNull Object obj) throws IOException {
        try {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            out.println(JSONObject.toJSONString(obj));
            out.flush();
        } catch (IOException e) {
            log.error("httpServletResponse输出响应出现异常", e);
            throw e;
        }
    }
}
