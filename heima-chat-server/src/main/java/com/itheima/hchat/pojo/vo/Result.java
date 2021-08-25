package com.itheima.hchat.pojo.vo;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description: 将返回给客户端的数据封装到实体中
 * @author: Kang Yong
 * @date: 2021/8/24 11:15
 * @version: v1.0
 */
public class Result {

    private boolean success; // 是否操作成功
    private String message; //
    private Object result;

    public Result() {
    }

    public Result(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public Result(boolean success, String message, Object result) {
        this.success = success;
        this.message = message;
        this.result = result;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
