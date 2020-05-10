package com.dq;

public enum FunctionType {
    SINGLE_CHART(1,"一对一单聊"),PEOPLE_CHART(2,"多人群聊"),FILE(3,"文件功能"),QUIT(4,"退出功能"),LOGIN(5,"退出功能");
    int code;
    String msg;

    FunctionType(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
