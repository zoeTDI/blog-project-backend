package cn.caldm.www.shared_kernel.utils;

/**
 *
 *
 *
 * @author caldm
 */
public class StringUtils {
    public static boolean isBlank(String arg) {
        return arg == null || arg.isBlank();
    }

    public static boolean isEmpty(String arg) {
        return arg == null || arg.isEmpty();
    }
}
