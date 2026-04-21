package com.example.request;

import org.apache.commons.lang3.StringUtils;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

public class HttpUtils {

    /**
     * GET请求
     *
     * @param address
     * @param header
     * @return
     * @throws Exception
     */
    public static String get(String address, Map<String, String> header, Map<String, String> params) throws Exception {
        return proxyHttpRequest(address + "?" + getRequestBody(header, true),
                "GET", header, params);
    }

    /**
     * POST请求
     *
     * @param address 请求地址
     * @param header  参数
     * @return
     * @throws Exception
     */
    public static String post(String address, Map<String, String> header, Map<String, String> params) throws Exception {
        return proxyHttpRequest(address, "POST", header, params);
    }

    /**
     * HTTP请求
     *
     * @param address 地址
     * @param method  方法
     * @param header  头信息
     * @return
     * @throws Exception
     */
    public static String proxyHttpRequest(String address, String method, Map<String, String> header,
                                          Map<String, String> params) throws Exception {
        String result = null;
        HttpURLConnection httpConnection = null;

        try {
            httpConnection = createConnection(address, method, header, getRequestBody(params, true));
            String encoding = "UTF-8";
            if (httpConnection.getContentType() != null
                    && httpConnection.getContentType().indexOf("charset=") >= 0) {
                encoding = httpConnection.getContentType()
                        .substring(httpConnection.getContentType().indexOf("charset=") + 8);
            }
            result = inputStream2String(httpConnection.getInputStream(), encoding);
        } catch (Exception e) {
            throw e;
        } finally {
            if (httpConnection != null) {
                httpConnection.disconnect();
            }
        }
        return result;
    }

    /**
     * 将参数化为 body
     *
     * @param params
     * @return
     */
    public static String getRequestBody(Map<String, String> params, boolean urlEncode) {
        StringBuilder body = new StringBuilder();
        if (null == params || params.isEmpty()) {
            return body.toString();
        }
        Iterator<String> iteratorHeader = params.keySet().iterator();
        try {
            while (iteratorHeader.hasNext()) {
                String key = iteratorHeader.next();
                String value = params.get(key);

                if (urlEncode && StringUtils.isNotBlank(key)) {
                    body.append(key + "=" + (value==null?"null":URLEncoder.encode(value, "UTF-8"))
                            + "&");
                } else {
                    body.append(key + "=" + value + "&");
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (body.length() == 0) {
            return "";
        }
        return body.substring(0, body.length() - 1);
    }

    /**
     * 创建HTTP连接
     *
     * @param url    地址
     * @param method 方法
     * @param header 头信息
     * @return
     * @throws Exception
     */
    private static HttpURLConnection createConnection(String url, String method, Map<String, String> header,
                                                      String jsonBody) throws Exception {
        URL Url = new URL(url);
        trustAllHttpsCertificates();
        HttpURLConnection httpConnection = (HttpURLConnection) Url.openConnection();



        // 设置请求时间
        httpConnection.setConnectTimeout(45000);
        // 设置 header
        if (header != null) {
            Iterator<String> iteratorHeader = header.keySet().iterator();
            while (iteratorHeader.hasNext()) {
                String key = iteratorHeader.next();
                httpConnection.setRequestProperty(key, header.get(key));
            }
        }
//        httpConnection.setRequestProperty("Content-Type",
//                "application/x-www-form-urlencoded;charset=UTF-8");

        // 设置请求方法
        httpConnection.setRequestMethod(method);
        httpConnection.setDoOutput(true);
        httpConnection.setDoInput(true);
        // 写query数据流
        if (StringUtils.isNotBlank(jsonBody.trim())) {
            OutputStream writer = null;
            try {
                if(null != httpConnection.getOutputStream()) {
                    writer = httpConnection.getOutputStream();
                    writer.write(jsonBody.getBytes("UTF-8"));
                }
            }catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (writer != null) {
                    writer.flush();
                    writer.close();
                }
            }
        }

        // 请求结果
        int responseCode = httpConnection.getResponseCode();
        if (responseCode != 200) {
            throw new Exception(
                    responseCode + ":" + inputStream2String(httpConnection.getErrorStream(), "UTF-8"));
        }

        return httpConnection;
    }

    /**
     * 设置 https 请求
     *
     * @throws Exception
     */
    private static void trustAllHttpsCertificates() throws Exception {
        HttpsURLConnection.setDefaultHostnameVerifier((str, session) -> true);
        javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
        javax.net.ssl.TrustManager tm = new TrustManager();
        trustAllCerts[0] = tm;
        javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, null);
        javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    }

    /**
     * 读取inputStream 到 string
     *
     * @param input
     * @param encoding
     * @return
     * @throws IOException
     */
    private static String inputStream2String(InputStream input, String encoding) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input, encoding));
        StringBuilder result = new StringBuilder();
        String temp = null;
        while ((temp = reader.readLine()) != null) {
            result.append(temp);
        }

        return result.toString();

    }

    // 设置 https 请求证书
    static class TrustManager implements javax.net.ssl.TrustManager, javax.net.ssl.X509TrustManager {

        @Override
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public boolean isServerTrusted(java.security.cert.X509Certificate[] certs) {
            return true;
        }

        public boolean isClientTrusted(java.security.cert.X509Certificate[] certs) {
            return true;
        }

        @Override
        public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType)
                throws java.security.cert.CertificateException {
            return;
        }

        @Override
        public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType)
                throws java.security.cert.CertificateException {
            return;
        }

    }
}
