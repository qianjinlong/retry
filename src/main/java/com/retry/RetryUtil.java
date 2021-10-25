package com.retry;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 类功能说明
 *
 * @author qianjinlong
 * @email 1277977370@qq.com
 * @date 2021/10/23
 */
public class RetryUtil {

    /** 默认重试次数 */
    private static final int DEFAULT_RETRY_COUNT = 3;

    /** 默认休眠时间 毫秒 */
    private static final long DEFAULT_SLEEP_TIME = 3000L;


    /**
     * 重试调度方法
     *
     * @param dataSupplier 方法执行体(返回数据)
     * @param <T>          返回数据类型
     * @return T
     * @throws RetryBusinessException 业务异常
     */
    public static <T> T retry(DataSupplier<T> dataSupplier) throws RetryBusinessException {
        return retry(dataSupplier, null, DEFAULT_RETRY_COUNT, DEFAULT_SLEEP_TIME, null);
    }

    /**
     * 重试调度方法
     *
     * @param dataSupplier 方法执行体(返回数据)
     * @param retryCount   重试次数
     * @param sleepTime    重试间隔睡眠时间(注意：阻塞当前线程)
     * @param <T>          返回数据类型
     * @return T
     * @throws RetryBusinessException 业务异常
     */
    public static <T> T retry(DataSupplier<T> dataSupplier, int retryCount, long sleepTime) throws RetryBusinessException {
        return retry(dataSupplier, null, retryCount, sleepTime, null);
    }

    /**
     * 对每一次失败进行调度(包括第一次执行)
     *
     * @param dataSupplier 方法执行体(返回数据)
     * @param consumer     出错异常处理(包括第一次执行和重试错误)
     */
    public static void retryAnyFail(DataSupplier<?> dataSupplier, Consumer<Throwable> consumer) {
        try {
            retry(dataSupplier, consumer, DEFAULT_RETRY_COUNT, DEFAULT_SLEEP_TIME, null);
        } catch (RetryBusinessException e) {
            // 业务失败
        }
    }

    /**
     * 对每一次失败进行调度(包括第一次执行)
     *
     * @param dataSupplier 方法执行体(返回数据)
     * @param consumer     出错异常处理(包括第一次执行和重试错误)
     * @param retryCount   重试次数
     * @param sleepTime    重试间隔睡眠时间(注意：阻塞当前线程)
     */
    public static void retryAnyFail(DataSupplier<?> dataSupplier, Consumer<Throwable> consumer, int retryCount, long sleepTime) {
        try {
            retry(dataSupplier, consumer, retryCount, sleepTime, null);
        } catch (RetryBusinessException e) {
            // 业务失败
        }
    }

    /**
     * 重试调度方法和处理
     *
     * @param dataSupplier 方法执行体(返回数据)
     * @param other        其他操作
     * @param <T>          返回数据类型
     * @return T
     */
    public static <T> T retrySuccessOrElseGet(DataSupplier<T> dataSupplier, Supplier<? extends T> other) {
        return retrySuccessOrElseGet(dataSupplier, null, DEFAULT_RETRY_COUNT, DEFAULT_SLEEP_TIME, null, other);
    }

    /**
     * 重试调度方法
     *
     * @param dataSupplier 方法执行体(返回数据)
     * @param other        其他操作
     * @param retryCount   重试次数
     * @param sleepTime    重试间隔睡眠时间(注意：阻塞当前线程)
     * @param <T>          返回数据类型
     * @return T
     */
    public static <T> T retrySuccessOrElseGet(DataSupplier<T> dataSupplier, Supplier<? extends T> other, int retryCount, long sleepTime) {
        return retrySuccessOrElseGet(dataSupplier, null, retryCount, sleepTime, null, other);
    }

    /**
     * 重试调度方法和处理
     *
     * @param dataSupplier 方法执行体(返回数据)
     * @param fn           处理业务成功失败
     * @param <T>          接收类型
     * @param <R>          返回类型
     * @return R
     */
    public static <T, R> R retryAnyFn(DataSupplier<T> dataSupplier, BiFunction<? super T, Throwable, ? extends R> fn) {
        return retryAnyFn(dataSupplier, null, DEFAULT_RETRY_COUNT, DEFAULT_SLEEP_TIME, null, fn);
    }

    /**
     * 重试调度方法和处理
     *
     * @param dataSupplier 方法执行体(返回数据)
     * @param fn           处理业务成功失败
     * @param retryCount   重试次数
     * @param sleepTime    重试间隔睡眠时间(注意：阻塞当前线程)
     * @param <T>          接收类型
     * @param <R>          返回类型
     * @return R
     */
    public static <T, R> R retryAnyFn(DataSupplier<T> dataSupplier,
                                      BiFunction<? super T, Throwable, ? extends R> fn,
                                      int retryCount,
                                      long sleepTime) {
        return retryAnyFn(dataSupplier, null, retryCount, sleepTime, null, fn);
    }

    /**
     * 重试调度方法和处理
     *
     * @param dataSupplier     方法执行体(返回数据)
     * @param exceptionCaught  出错异常处理(包括第一次执行和重试错误)
     * @param retryCount       重试次数
     * @param sleepTime        重试间隔睡眠时间(注意：阻塞当前线程)
     * @param expectExceptions 期待异常(抛出符合相应异常时重试), 空或者空容器默认进行重试
     * @param other            其他操作
     * @param <T>              返回数据类型
     * @return T
     */
    public static <T> T retrySuccessOrElseGet(DataSupplier<T> dataSupplier,
                                              Consumer<Throwable> exceptionCaught,
                                              int retryCount,
                                              long sleepTime,
                                              List<Class<? extends Throwable>> expectExceptions,
                                              Supplier<? extends T> other) {
        try {
            return retry(dataSupplier, exceptionCaught, retryCount, sleepTime, expectExceptions);
        } catch (RetryBusinessException e) {
            // 业务异常处理
            return null == other ? null : other.get();
        }
    }

    /**
     * 重试调度方法和处理
     *
     * @param dataSupplier     方法执行体(返回数据)
     * @param exceptionCaught  出错异常处理(包括第一次执行和重试错误)
     * @param retryCount       重试次数
     * @param sleepTime        重试间隔睡眠时间(注意：阻塞当前线程)
     * @param expectExceptions 期待异常(抛出符合相应异常时重试), 空或者空容器默认进行重试
     * @param fn               处理业务成功失败
     * @param <T>              接收类型
     * @param <R>              返回类型
     * @return R
     */
    public static <T, R> R retryAnyFn(DataSupplier<T> dataSupplier,
                                      Consumer<Throwable> exceptionCaught,
                                      int retryCount,
                                      long sleepTime,
                                      List<Class<? extends Throwable>> expectExceptions,
                                      BiFunction<? super T, Throwable, ? extends R> fn) {
        T retry = null;
        Throwable ex = null;
        try {
            retry = retry(dataSupplier, exceptionCaught, retryCount, sleepTime, expectExceptions);
        } catch (Throwable throwable) {
            // 业务失败
            ex = throwable;
        }
        return fn.apply(retry, ex);
    }

    /**
     * 重试调度方法
     *
     * @param dataSupplier     方法执行体(返回数据)
     * @param exceptionCaught  出错异常处理(包括第一次执行和重试错误)
     * @param retryCount       重试次数
     * @param sleepTime        重试间隔睡眠时间(注意：阻塞当前线程)
     * @param expectExceptions 期待异常(抛出符合相应异常时重试), 空或者空容器默认进行重试
     * @param <T>              返回数据类型
     * @return T
     * @throws RetryBusinessException 业务异常
     */
    public static <T> T retry(DataSupplier<T> dataSupplier,
                              Consumer<Throwable> exceptionCaught,
                              int retryCount,
                              long sleepTime,
                              List<Class<? extends Throwable>> expectExceptions) throws RetryBusinessException {
        Throwable ex;
        try {
            // 产生数据
            return dataSupplier == null ? null : dataSupplier.get();
        } catch (Throwable throwable) {
            ex = throwable;
            // 捕获异常
            catchException(exceptionCaught, throwable);
        }

        // 校验异常是否匹配期待异常
        if (expectExceptions != null && ! expectExceptions.isEmpty()) {
            Class<? extends Throwable> exClass = ex.getClass();
            boolean match = expectExceptions.stream().anyMatch(clazz -> clazz == exClass);
            if (! match) {
                return null;
            }
        }

        // 匹配期待异常或者允许任何异常重试
        for (int i = 0; i < retryCount; i++) {
            try {
                if (sleepTime > 0) {
                    Thread.sleep(sleepTime);
                }
                return dataSupplier.get();
            } catch (InterruptedException e) {
                System.out.println("thread interrupted !! break retry, cause: " + e.getMessage());
                // 恢复中断信号
                Thread.currentThread().interrupt();
                // 线程中断直接退出重试
                break;
            } catch (Exception throwable) {
                catchException(exceptionCaught, throwable);
            }
        }

        // 重试结束, 抛出业务异常
        throw (RetryBusinessException) ex.getCause();
    }

    private static void catchException(Consumer<Throwable> exceptionCaught, Throwable throwable) {
        try {
            if (exceptionCaught != null) {
                exceptionCaught.accept(throwable);
            }
        } catch (Throwable e) {
            System.out.println("retry exception caught throw error: " + e.getMessage());
        }
    }

    /**
     * 函数式接口可以抛出异常
     *
     * @param <T> 返回数据类型
     */
    @FunctionalInterface
    private interface DataSupplier<T> {
        /**
         * Gets a result.
         *
         * @return a result
         * @throws Exception 错误时候抛出异常
         */
        T get() throws Exception;
    }

    /** 自定义业务异常 */
    private static class RetryBusinessException extends Exception {
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


    public static void main(String[] args) {

        String s = RetryUtil.retryAnyFn(() -> {
            System.out.println("dataSupplier");
            int a = 1 / 0;
            return "";
        }, (o, e) -> {
            System.out.println("fn");
            return "123";
        }, 4, 300);

        System.out.println(s);
    }
}
