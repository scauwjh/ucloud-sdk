package me.keiwu.ucloud.beans;

import me.keiwu.ucloud.enums.ReturnCodeEnum;

/**
 * Created by kei on 8/3/16.
 */
public class Response {

    private int retCode;
    private String message;

    private Response(int retCode, String message) {
        this.retCode = retCode;
        this.message = message;
    }

    public static Response ok() {
        return new Response(ReturnCodeEnum.OK.getCode(), "");
    }

    public static Response error(int retCode, String errMsg) {
        return new Response(retCode, errMsg);
    }


    public int getRetCode() {
        return retCode;
    }

    public void setRetCode(int retCode) {
        this.retCode = retCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
