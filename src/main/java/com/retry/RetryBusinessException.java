package com.retry;

/**
 * 自定义业务异常
 *
 * @author qianjinlong
 * @email 1277977370@qq.com
 * @date 2021/10/28
 */
public class RetryBusinessException extends Exception {
    public RetryBusinessException() {
        super();
    }

    public RetryBusinessException(String message) {
        super(message);
    }

    public RetryBusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public RetryBusinessException(Throwable cause) {
        super(cause);
    }
}
