package com.example.redisstudy.common.result;

/**
 * @author MrWei
 * @version 1.0.0
 * @date 2023/1/3 19:02
 * @description 通用返回状态码
 */
public enum ResultCode {
    /* 成功状态码 */
    SUCCESS(1, "成功"),

    /* 参数错误：10001-19999 */
    PARAM_IS_INVALID(10001, "参数无效"),
    PARAM_IS_BLANK(10002, "参数为空"),
    PARAM_TYPE_BIND_ERROR(10003, "参数类型错误"),
    PARAM_NOT_COMPLETE(10004, "参数缺失"),
    METHOD_ARGUMENT_NOT_VALID(10005,"参数非法"),
    MESSAGE_NOT_READABLE(10006,"http解析请求参数异常"),
    HTTP_MEDIA_TYPE(10007,"媒体类型异常"),
    AUTHENTICATION_EXCEPTION(10008,"登录授权错误"),
    UN_AUTHENTICATED_EXCEPTION(10009,"未认证"),
    BAD_SQL_GRAMMAR(10010,"SQL语法异常"),
    SYSTEM_EXCEPTION(10011,"系统性一般错误"),
    SQL_INTEGRITY_CONSTRAINT_VIOLATION(10012,"字段丢失"),
    LOWER_AUTHORIZATION(10013,"权限不足"),

    /* 用户错误：20001-29999*/
    USER_NOT_SIGN_IN(20001, "用户未登录"),
    USER_PASSWORD_ERROR(20002, "密码错误"),
    USER_ACCOUNT_ERROR(20003, "账号错误"),
    USER_VERIFY_CODE_ERROR(20004, "验证码错误"),
    USER_CODE_TIMEOUT(20005, "验证码失效"),
    USER_ACCOUNT_FORBIDDEN(20006, "账号已被禁用"),
    USER_SIGN_UP_FAIL(20007, "用户注册失败"),
    USER_SIGN_IN_FAIL(20008, "用户登录失败"),
    USER_BIND_PHONE_FAIL(20009, "手机号绑定失败"),
    USER_SMS_EXIST(20010,"验证码已发送"),
    USER_SMS_NOT_EXIST(20011,"请重新发送验证码"),
    USER_NOT_LOGGED_IN(20012, "用户未登录"),
    USER_ACCOUNT_NOT_EXIST(20013,"账号信息不存在"),
    USER_ACCOUNT_LOCKED(20014,"账号已被锁定"),
    USER_AUTHENTICATE_FAIL(20015,"用户无资格"),


    /* 业务错误：30001-39999 */
    SMS_ERROR(30001, "短信业务出现问题"),
    UPLOAD_ERROR(30002, "上传文件业务出现问题"),
    REDIS_SET_ERROR(30003,"Redis缓存插入失败"),

    /* 系统错误：40001-49999 */
    SYSTEM_INNER_ERROR(40001, "系统繁忙，请稍后重试"),
    SYSTEM_TOKEN_ERROR(40002,"token无效"),

    /* 数据错误：50001-599999 */
    RESULT_CODE_DATA_NONE(50001, "数据未找到"),
    DATA_IS_WRONG(50002, "数据有误"),
    DATA_ALREADY_EXISTED(50003, "数据已存在"),
    DATABASE_ERROR(50004, "数据库操作异常"),

    /* 接口错误：60001-69999 */
    INTERFACE_INNER_INVOKE_ERROR(60001, "内部系统接口调用异常"),
    INTERFACE_OUTER_INVOKE_ERROR(60002, "外部系统接口调用异常"),
    INTERFACE_FORBID_VISIT(60003, "该接口禁止访问"),
    INTERFACE_ADDRESS_INVALID(60004, "接口地址无效"),
    INTERFACE_REQUEST_TIMEOUT(60005, "接口请求超时"),
    INTERFACE_EXCEED_LOAD(60006, "接口负载过高"),
    INTERFACE_FALLBACK(60007, "接口被降级"),

    LOTTERY_ACTIVITY_NOT_START_OR_END(70001,"活动未开始或已结束"),
    LOTTERY_ACTIVITY_BUSY(70002,"活动繁忙"),
    LOTTERY_FAIL(70003,"抽奖失败"),
    LOTTERY_NOT_SUCCESS(70004,"未中奖"),


    /* 权限错误：70001-79999 */
    PERMISSION_NO_ACCESS(90001, "无访问权限");

    private final Integer code;

    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer code() {
        return this.code;
    }

    public String message() {
        return this.message;
    }

    public static String getMessage(String name) {
        for (ResultCode item : ResultCode.values()) {
            if (item.name().equals(name)) {
                return item.message;
            }
        }
        return name;
    }

    public static Integer getCode(String name) {
        for (ResultCode item : ResultCode.values()) {
            if (item.name().equals(name)) {
                return item.code;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.name();
    }

}

