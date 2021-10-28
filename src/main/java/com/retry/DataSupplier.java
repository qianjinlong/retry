package com.retry;

/**
 * 函数式接口可以抛出异常
 *
 * @param <T> 返回数据类型
 * @author qianjinlong
 * @email 1277977370@qq.com
 * @date 2021/10/28
 */
@FunctionalInterface
public interface DataSupplier<T> {
    /**
     * Gets a result.
     *
     * @return a result
     * @throws Exception 错误时候抛出异常
     */
    T get() throws Exception;
}
