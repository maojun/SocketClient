package com.gable.socket.utils;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.log4j.Logger;

public class HttpHelper {
	private final static Logger LOG = Logger.getLogger(HttpHelper.class);


    private HttpHelper() {
    }


    /** 
    * @Title: postRequest_ResponseBodyAsString 
    * @Description: http post请求,application/x-www-form-urlencoded方式
    * @param url
    * @param parameters
    * @return
    * @throws Exception
    * @throws 
    */
    public static String postRequestResponseBodyAsString(String url,
            Map<String, String> parameters) throws RuntimeException {
        return postRequestResponseBodyAsString(null, url, parameters, 8000,
                20000);
    }


    /** 
    * @Title: postRequest_ResponseBodyAsString 
    * @Description: http post请求,application/x-www-form-urlencoded方式
    * @param header
    * @param url
    * @param parameters
    * @return
    * @throws Exception
    * @throws 
    */
    public static String postRequestResponseBodyAsString(
            List<Header> headerList, String url, Map<String, String> parameters)
            throws RuntimeException {
        return postRequestResponseBodyAsString(headerList, url, parameters,
                8000, 20000);
    }


    /** 
    * @Title: postRequestResponseBodyAsString 
    * @Description: http post请求,application/x-www-form-urlencoded方式
    * @param header
    * @param url
    * @param parameters
    * @param contimeout
    * @param sotimeout
    * @return
    * @throws Exception
    * @throws 
    */
    public static String postRequestResponseBodyAsString(
            List<Header> headerList, String url, Map<String, String> parameters,
            Integer contimeout, Integer sotimeout) throws RuntimeException {
        HttpClient httpClient = new HttpClient();
        httpClient.getHttpConnectionManager().getParams()
                .setSoTimeout(sotimeout);
        httpClient.getHttpConnectionManager().getParams()
                .setConnectionTimeout(contimeout);
        // httpClient.getHostConfiguration().setProxy("proxy2.fn.com",8080);
        PostMethod method = new PostMethod(url);
        //关闭cookie
        method.getParams().setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
        int statusCode;
        String responseCharSet = "";
        String responseString = "";
        if (null != headerList && headerList.size() > 0) {
            for (Header header : headerList) {
                method.addRequestHeader(header);
            }
        }
        try {
            for (String key : parameters.keySet()) {
                method.addParameter(key, parameters.get(key));
            }
            method.addRequestHeader("Content-Type",
                    "application/x-www-form-urlencoded;charset=UTF-8");
            statusCode = httpClient.executeMethod(method);
            if (statusCode != HttpStatus.SC_OK) {
                LOG.error(">>>>>>>>>>>>>>>  Method failed: "
                        + method.getStatusLine());
                LOG.error(">>>>>>>>>>>>>>>  Http服务链路异常:服务器状态码为" + statusCode);
            }
            responseCharSet = method.getResponseCharSet();
            responseString = method.getResponseBodyAsString();
            if ("ISO-8859-1".equals(responseCharSet)) {
                responseString = new String(
                        responseString.getBytes(responseCharSet), "UTF-8");
            }


        } catch (Exception e) {
            LOG.error(">>>>>>>>>>>>>>>  Http服务链路异常:" + e.getMessage() + e);
            throw new RuntimeException(
                    ">>>>>>>>>>>>>>>  Http服务链路异常:" + e.getMessage() + e);
        } finally {
            method.releaseConnection();
        }
        return responseString;
    }


    /** 
    * @Title: postRequestResponseBodyAsString 
    * @Description: json
    * @param url
    * @param str
    * @return
    * @throws Exception
    * @throws 
    */
    public static String postRequestResponseBodyAsString(String url, String str)
            throws RuntimeException {
        return postRequestResponseBodyAsString(null, url, str, 8000, 20000);
    }


    /** 
        * @Title: postRequestResponseBodyAsString 
        * @Description: json
        * @param header
        * @param url
        * @param str
        * @return
        * @throws Exception
        * @throws 
        */
    public static String postRequestResponseBodyAsString(
            List<Header> headerList, String url, String str)
            throws RuntimeException {
        return postRequestResponseBodyAsString(headerList, url, str, 8000,
                20000);
    }


    /**
     * @throws Exception  
    * @Title: postRequestResponseBodyAsString 
    * @Description: http post请求,application/json;charset=UTF-8方式
    * @param header
    * @param url
    * @param str
    * @param contimeout
    * @param sotimeout
    * @return
    * @throws Exception
    * @throws 
    */
    public static String postRequestResponseBodyAsString(
            List<Header> headerList, String url, String str, Integer contimeout,
            Integer sotimeout) throws RuntimeException {
        HttpClient httpClient = new HttpClient();
        httpClient.getHttpConnectionManager().getParams()
                .setSoTimeout(sotimeout);
        httpClient.getHttpConnectionManager().getParams()
                .setConnectionTimeout(contimeout);
        PostMethod method = new PostMethod(url);
        //关闭cookie
        method.getParams().setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
        int statusCode;
        String responseCharSet = "";
        String responseString = "";
        if (null != headerList && headerList.size() > 0) {
            for (Header header : headerList) {
                method.addRequestHeader(header);
            }
        }
        try {
            // String json = JSON.toJSONString(o);
            StringRequestEntity entity = new StringRequestEntity(str,
                    "application/json", "utf-8");// 解决中文乱码问题
            method.setRequestEntity(entity);
            method.addRequestHeader("Content-Type",
                    "application/json;charset=UTF-8");
            statusCode = httpClient.executeMethod(method);
            if (statusCode != HttpStatus.SC_OK) {
                LOG.error(">>>>>>>>>>>>>> Method failed: "
                        + method.getStatusLine());
                LOG.error(">>>>>>>>>>>>>> Http服务链路异常:服务器状态码为" + statusCode);
            }
            responseCharSet = method.getResponseCharSet();
            responseString = method.getResponseBodyAsString();
            if ("ISO-8859-1".equals(responseCharSet)) {
                responseString = new String(
                        responseString.getBytes(responseCharSet), "UTF-8");
            }


        } catch (Exception e) {
            LOG.error(">>>>>>>>>>>>>>  Http服务链路异常:" + e.getMessage() + e);
            throw new RuntimeException(
                    ">>>>>>>>>>>>>>>  Http服务链路异常:" + e.getMessage() + e);
        } finally {
            method.releaseConnection();
        }
        return responseString;
    }
    
    /**
     * @throws Exception 
     * 
    * @Title: getRequestResponseBodyAsString 
    * @Description: http get请求,application/json;charset=UTF-8方式
    * @param headerList
    * @param url
    * @param charset
    * @return
    * @throws
     */
    public static String getRequestResponseBodyAsString(List<Header> headerList,
            String url, Charset charset, Map<String, String> params)
            throws RuntimeException {
        return getRequestResponseBodyAsString(headerList, url, charset, 8000,
                20000, params);
    }


    public static String getRequestResponseBodyAsString(List<Header> headerList,
            String url, Charset charset, Integer contimeout, Integer sotimeout,
            Map<String, String> params) throws RuntimeException {
        GetMethod method = new GetMethod(url);
        //关闭cookie
        method.getParams().setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
        String responseCharSet = "";
        String responseString = "";
        if (null != headerList && headerList.size() > 0) {
            for (Header header : headerList) {
                method.addRequestHeader(header);
            }
        }


        if (params != null && params.size() > 0) {
            List<NameValuePair> paramList = new ArrayList<>();
            for (String key : params.keySet()) {
                paramList.add(new NameValuePair(key, params.get(key)));
            }
            NameValuePair[] paramArray = paramList
                    .toArray(new NameValuePair[0]);
            method.setQueryString(paramArray);
        }


        try {
            HttpClient httpClient = new HttpClient();
            httpClient.getHttpConnectionManager().getParams()
                    .setSoTimeout(sotimeout);
            httpClient.getHttpConnectionManager().getParams()
                    .setConnectionTimeout(contimeout);
            int status = httpClient.executeMethod(method);
            responseCharSet = method.getResponseCharSet();
            responseString = method.getResponseBodyAsString();
            if (status == HttpStatus.SC_OK) {
                if ("ISO-8859-1".equals(responseCharSet)) {
                    responseString = new String(
                            responseString.getBytes(responseCharSet), charset);
                }
            } else {
                LOG.error(">>>>>>>>>>>>>> Method failed: "
                        + method.getStatusLine());
                LOG.error(">>>>>>>>>>>>>> Http服务链路异常:服务器状态码为" + status);
            }
        } catch (Exception e) {
            LOG.error(">>>>>>>>>>>>>>  Http服务链路异常:" + e.getMessage() + e);
            throw new RuntimeException(
                    ">>>>>>>>>>>>>>>  Http服务链路异常:" + e.getMessage() + e);
        } finally {
            // 释放连接
            method.releaseConnection();
        }
        return responseString;
    }
    /** 
     * @Title: getFile 
     * @Description: 拿到文件内容
     * @param url 地址
     * @param charset 编码
     * @return
     * @throws 
     */ 
     public static String getFile(String url,Charset charset) {
         return getFile(url, charset,8000,20000);
     }
     
     public static String getFile(String url,Charset charset,Integer contimeout,Integer sotimeout) {
         GetMethod method = new GetMethod(url);
         //关闭cookie
         method.getParams().setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
         String responseCharSet ="";
         String responseString = "";
         try {
             HttpClient httpClient = new HttpClient();
             httpClient.getHttpConnectionManager().getParams().setSoTimeout(sotimeout);
             httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(contimeout);
             int status = httpClient.executeMethod(method);
             responseCharSet = method.getResponseCharSet();
             responseString = method.getResponseBodyAsString();
             if (status == HttpStatus.SC_OK) {
                 if ("ISO-8859-1".equals(responseCharSet)) {
                     responseString = new String(responseString.getBytes(responseCharSet), charset);
                 }
             } else {
                 LOG.error(">>>>>>>>>>>>>> Method failed: " + method.getStatusLine());
                 LOG.error(">>>>>>>>>>>>>> Http服务链路异常:服务器状态码为" + status);
             }
         } catch (Exception e) {
             e.printStackTrace();
         } finally {
             // 释放连接
             method.releaseConnection();
         }
         return responseString;
     }
}
