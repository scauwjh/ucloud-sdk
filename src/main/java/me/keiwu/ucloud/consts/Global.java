package me.keiwu.ucloud.consts;

import com.google.common.base.Charsets;

import java.nio.charset.Charset;

/**
 * Created by kei on 8/3/16.
 */
public class Global {

    public static final Charset CHARSET = Charsets.UTF_8;

    public static final String BASE_API_URL = "https://api.ucloud.cn";

    public static final String BASE_PUT_URL = "http://{0}.ufile.ucloud.cn";

    public static final String BASE_POST_URL = "http://{0}.ufile.ucloud.cn/{1}";

}
