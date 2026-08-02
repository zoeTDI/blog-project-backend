package cn.caldm.www.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * 全局日志工具类
 *
 * @author caldm
 */
public class LogUtils {
    private static Logger getLogger() {
        String className = new Throwable().getStackTrace()[2].getClassName();
        return LoggerFactory.getLogger(className);
    }

    public static void info(String message) {
        getLogger().info(message);
    }

    public static void info(String format, Object... arguments) {
        getLogger().info(format, arguments);
    }

    public static void debug(String message) {
        getLogger().debug(message);
    }

    public static void warn(String message) {
        getLogger().warn(message);
    }

    public static void error(String message) {
        getLogger().error(message);
    }

    public static void error(String message, Throwable t) {
        getLogger().error(message, t);
    }
}
