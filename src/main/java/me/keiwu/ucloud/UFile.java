package me.keiwu.ucloud;

import com.alibaba.fastjson.JSON;
import me.keiwu.ucloud.enums.ReturnCodeEnum;
import me.keiwu.ucloud.beans.Config;
import me.keiwu.ucloud.beans.Response;
import me.keiwu.ucloud.consts.Global;
import me.keiwu.ucloud.enums.ActionEnums;
import me.keiwu.ucloud.enums.ParamKeyEnums;
import me.keiwu.ucloud.enums.TypeEnums;
import me.keiwu.ucloud.utils.HttpUtil;
import me.keiwu.ucloud.utils.SignatureUtil;
import com.google.common.collect.Maps;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import jodd.http.HttpRequest;
import jodd.http.HttpResponse;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Map;

/**
 * Created by kei on 8/1/16.
 */
public class UFile {

    private Config config;

    public UFile(Config config) {
        this.config = config;
    }


    /**
     * 创建bucket
     * @param bucketName
     * @return
     */
    public Response createBucket(String bucketName) {
        return sendAction(ActionEnums.CreateBucket, bucketName);
    }

    /**
     * 删除bucket
     * @param bucketName
     * @return
     */
    public Response deleteBucket(String bucketName) {
        return sendAction(ActionEnums.DeleteBucket, bucketName);
    }

    /**
     * 更新bucket属性
     * @param bucketName
     * @param type
     * @return
     */
    public Response updateBucket(String bucketName, TypeEnums type) {
        Map<String, String> params = Maps.newHashMap();
        params.put(ParamKeyEnums.Type.name(), type.name().toLowerCase());
        return sendAction(ActionEnums.UpdateBucket, bucketName, params);
    }

    /**
     * 上传文件(put)
     * @param bucket
     * @param saveKey
     * @param content
     * @param contentType
     * @return
     */
    public Response putFile(String bucket, String saveKey, byte[] content, String contentType) {
        String url = MessageFormat.format(Global.BASE_PUT_URL, bucket);
        HttpRequest request = HttpUtil.preparePut(url).contentType(contentType);
        // set params
        String authorization = SignatureUtil.getAuthorization(request, this.config, bucket, saveKey);
        HttpResponse response = request.body(content, contentType)
                .header(ParamKeyEnums.Authorization.name(), authorization)
                .path(saveKey)
                .send();
        Response ret = Response.ok();
        ret.setMessage(response.bodyText());
        return ret;
    }


    /**
     * 上传文件(post)
     * @param bucket
     * @param saveKey
     * @param file
     * @param contentType
     * @return
     */
    public Response postFile(String bucket, String saveKey, File file, String contentType) {
        String url = MessageFormat.format(Global.BASE_POST_URL, bucket, saveKey);
        HttpRequest request = HttpUtil.preparePost(url).contentType(contentType);
        // set params
        String authorization = SignatureUtil.getAuthorization(request, this.config, bucket, saveKey);
        HttpResponse response = request.form(ParamKeyEnums.Authorization.name(), authorization)
                .form(ParamKeyEnums.FileName.name(), saveKey)
                .form("tmp", file)
                .send();
        Response ret = Response.ok();
        ret.setMessage(response.bodyText());
        return ret;
    }


    /**
     * 秒传文件, 没有完成, 需要生成ETag作为hash
     * @param bucket
     * @param saveKey
     * @param file
     * @param contentType
     * @return
     */
    @Deprecated
    public Response postFileWithMD5(String bucket, String saveKey, File file, String contentType) {
        String url = MessageFormat.format(Global.BASE_POST_URL, bucket, saveKey);
        try {
            // create ETag as hash code
            String hash = Files.hash(file, Hashing.md5()).toString();
            Map<String, String> params = Maps.newHashMap();
            params.put("Hash", hash);
            params.put("FileName", saveKey);
            params.put("FileSize", file.length() + "");
            HttpRequest request = HttpUtil.preparePost(url).contentType(contentType);
            // set params
            String authorization = SignatureUtil.getAuthorization(request, this.config, bucket, saveKey);
            HttpResponse response = request.header(ParamKeyEnums.Authorization.name(), authorization)
                    .path("uploadhit?" + HttpUtil.genUrlParams(params))
                    .send();
            Response ret = Response.ok();
            ret.setMessage(response.bodyText());
            return ret;
        } catch (IOException e) {
            e.printStackTrace();
            return Response.error(ReturnCodeEnum.ERROR.getCode(), e.getMessage());
        }
    }






    /*****************************/
    /****** private methods ******/
    /*****************************/
    private Response sendAction(ActionEnums action, String bucketName) {
        Map<String, String> params = Maps.newHashMap();
        return sendAction(action, bucketName, params);
    }
    private Response sendAction(ActionEnums action, String bucketName, Map<String, String> params) {
        params.put(ParamKeyEnums.Action.name(), action.name());
        params.put(ParamKeyEnums.BucketName.name(), bucketName);
        params = SignatureUtil.spaceSignParams(params, this.config);
        String url = MessageFormat.format("{0}/?{1}", Global.BASE_API_URL, HttpUtil.genUrlParams(params));
        HttpResponse response = HttpUtil.prepareGet(url).send();
        Response resp = Response.ok();
        resp.setMessage(response.bodyText());
        return resp;
    }


}
