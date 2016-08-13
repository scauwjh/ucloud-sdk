package me.keiwu.ucloud.enums;

/**
 * Created by kei on 8/10/16.
 */
public enum ReturnCodeEnum {
    OK(0, "ok"),
    ERROR(-1, "未知异常"),
    ;

    private int code;
    private String msg;

    ReturnCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }

}
