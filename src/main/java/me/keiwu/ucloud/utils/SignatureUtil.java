package me.keiwu.ucloud.utils;

import com.alibaba.fastjson.JSON;
import jodd.http.HttpMultiMap;
import jodd.util.StringUtil;
import me.keiwu.ucloud.consts.Global;
import me.keiwu.ucloud.beans.Config;
import me.keiwu.ucloud.enums.DefultParamEnums;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.hash.Hashing;
import jodd.http.HttpRequest;
import org.apache.commons.codec.digest.HmacUtils;

import java.util.*;

/**
 * Created by kei on 8/3/16.
 */
public class SignatureUtil {


    /**
     * 空间操作签名
     * @param params
     * @param privateKey
     * @return
     */
    public static String spaceSign(Map<String, String> params, String privateKey) {
        StringBuilder sb = new StringBuilder();
        List<String> keyList = Lists.newArrayList(params.keySet());
        Collections.sort(keyList);

        for(String key : keyList) {
            sb.append(key).append(params.get(key));
        }

        sb.append(privateKey);
        return Hashing.sha1().hashString(sb.toString(), Global.CHARSET).toString();
    }

    /**
     * 文件操作签名
     * @param request
     * @param bucket
     * @param saveKey
     * @param privateKey
     * @return
     */
    public static String fileSign(HttpRequest request, String bucket, String saveKey, String privateKey) {
        String str2Sign = new StringBuilder()
                .append(request.method()).append("\n")
                .append("").append("\n")
                .append(request.contentType()).append("\n")
                .append("").append("\n")
                .append(canonicalizedUcloudHeaders(request))
                .append(canonicalizedResource(bucket, saveKey))
                .toString();
        return Base64.getEncoder().encodeToString(HmacUtils.hmacSha1(privateKey, str2Sign));
    }


    /**
     * 获取授权串
     * @param request
     * @param config
     * @param bucket
     * @param saveKey
     * @return
     */
    public static String getAuthorization(HttpRequest request, Config config, String bucket, String saveKey) {
        return "UCloud" + " " + config.getPublicKey() + ":" + fileSign(request, bucket, saveKey, config.getPrivateKey());
    }


    /**
     * 插入签名参数
     * @param params
     * @param config
     * @return
     */
    public static Map<String, String> spaceSignParams(Map<String, String> params, Config config) {
        params.put(DefultParamEnums.PublicKey.name(), config.getPublicKey());
        params.put(DefultParamEnums.Signature.name(), SignatureUtil.spaceSign(params, config.getPrivateKey()));
        return params;
    }


    /*****************************/
    /****** private methods ******/
    /*****************************/
    private static String canonicalizedUcloudHeaders(HttpRequest request) {
        List<Map.Entry<String, String>> headers = request.headers().entries();
        Map<String, List<String>> params = Maps.newHashMap();
        headers.stream().forEach(e -> {
            String key = e.getKey().toLowerCase();
            if (key.startsWith("x-ucloud-")) {
                String value = e.getValue();
                if (StringUtil.isNotBlank(value)) {
                    List<String> values = params.get(key);
                    if (values == null) values = Lists.newArrayList();
                    values.add(value);
                }
            }
        });

        StringBuilder sb = new StringBuilder();
        List<String> keyList = Lists.newArrayList(params.keySet());
        Collections.sort(keyList);
        keyList.stream().forEach(k -> {
            List<String> values = params.get(k);
            sb.append(k).append(":");
            boolean isFirst = true;
            for (String v : values) {
                if (isFirst) {
                    sb.append(v);
                    isFirst = false;
                } else {
                    sb.append(",").append(v);
                }
            }
            sb.append("\n");
        });
        return sb.toString();
    }

    private static String canonicalizedResource(String bucket, String key) {
        return new StringBuffer()
                .append("/")
                .append(bucket)
                .append("/")
                .append(key)
                .toString();
    }

}
