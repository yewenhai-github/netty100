package com.netty100.common.http;

import com.netty100.common.exception.CommonException;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;


/**
 * @author yewenhai
 * @version 1.0.0, 2022/3/23
 * @since 1.0.0, 2022/3/23
 */
@Slf4j
public class RemotingHttpUtil {

    // trust-https start
    private static void trustAllHosts(HttpsURLConnection connection) {
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            SSLSocketFactory newFactory = sc.getSocketFactory();

            connection.setSSLSocketFactory(newFactory);
        } catch (Exception e) {
            throw new CommonException(e);
        }
        connection.setHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
    }
    private static final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }
        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }
    }};
    // trust-https end

    public static RemotingHttpResult postBody(String url, String token, int timeout, Object requestObj, int retry){
        RemotingHttpResult rt = null;

        try {
            rt = postBody(url, token, timeout, requestObj);
        } catch (Exception e) {
            if(retry <= 3){
                retry ++;
                log.warn("http exception,重试机制触发，重试次数={}, uri={}", retry, url, e);
                return postBody(url,token, timeout, requestObj, retry);
            }else{
                throw new CommonException(e);
            }
        }
        return rt;
    }

    /**
     * post
     *
     * @param url
     * @param timeout
     * @param requestObj
     * @return
     */
    public static RemotingHttpResult postBody(String url,String token, int timeout, Object requestObj) {
        HttpURLConnection connection = null;
        BufferedReader bufferedReader = null;
        try {
            // connection
            URL realUrl = new URL(url);
            connection = (HttpURLConnection) realUrl.openConnection();

            // trust-https
            boolean useHttps = url.startsWith("https");
            if (useHttps) {
                HttpsURLConnection https = (HttpsURLConnection) connection;
                trustAllHosts(https);
            }

            // connection setting
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setReadTimeout(timeout * 1000);
            connection.setConnectTimeout(3 * 1000);
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            connection.setRequestProperty("Accept-Charset", "application/json;charset=UTF-8");
            connection.setRequestProperty("Authorization", token);

            // do connection
            connection.connect();

            // write requestBody
            if (requestObj != null) {
                String requestBody = GsonTool.toJson(requestObj);

                DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
                dataOutputStream.write(requestBody.getBytes("UTF-8"));
                dataOutputStream.flush();
                dataOutputStream.close();
            }

            // valid StatusCode
            int statusCode = connection.getResponseCode();
            InputStream is;
            if (statusCode == 200) {
                is = connection.getInputStream();
            }else{
                is = connection.getErrorStream();
            }
            // result
            bufferedReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }

            RemotingHttpResult rspResult = new RemotingHttpResult();
            rspResult.setResponseCode(statusCode);
            rspResult.setResponseStr(result.toString());
            return rspResult;
        } catch (IOException e) {
//            log.error("Http IOException, uri={}", url, e);
            throw new CommonException(e);
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (Exception e2) {
                throw new CommonException(e2);
            }
        }
    }

}
