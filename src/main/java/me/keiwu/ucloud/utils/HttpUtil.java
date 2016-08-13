package me.keiwu.ucloud.utils;

import me.keiwu.ucloud.consts.Global;
import com.google.common.base.Charsets;
import jodd.http.HttpRequest;
import jodd.http.HttpResponse;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Set;

/**
 * Created by kei on 8/10/16.
 */
public class HttpUtil {

    public static HttpRequest prepareGet(String uri) {
        return defaultSetting(HttpRequest.get(uri));
    }

    public static HttpRequest preparePost(String uri) {
        return defaultSetting(HttpRequest.post(uri));
    }

    public static HttpRequest preparePut(String uri) {
        return defaultSetting(HttpRequest.put(uri));
    }


    public static HttpResponse get(String url, Map<String, String> params) throws UnsupportedEncodingException {
        url = MessageFormat.format("{0}/?{1}", url, genUrlParams(params));
        return prepareGet(url).send();
    }

    public static String genUrlParams(Map<String, String> params)  {
        Set<String> keys = params.keySet();
        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        try {
            for (String k : keys) {
                String value = URLEncoder.encode(params.get(k), Global.CHARSET.name());
                if (isFirst) {
                    sb.append(k).append("=").append(value);
                    isFirst = false;
                } else {
                    sb.append("&").append(k).append("=").append(value);
                }
            }
            return sb.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }



    /*****************************/
    /****** private methods ******/
    /*****************************/
    private static HttpRequest defaultSetting(HttpRequest request) {
        return request.queryEncoding(Charsets.UTF_8.name());
    }

}
