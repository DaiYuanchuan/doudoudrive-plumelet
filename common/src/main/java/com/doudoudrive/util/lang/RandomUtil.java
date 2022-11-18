package com.doudoudrive.util.lang;

import com.doudoudrive.constant.NumberConstant;
import org.apache.commons.lang3.StringUtils;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * <p>随机工具类</p>
 * <p>2022-11-13 15:14</p>
 *
 * @author Dan
 **/
public class RandomUtil {

    /**
     * 用于随机选的数字
     */
    public static final String BASE_NUMBER = "0123456789";

    /**
     * 获取随机数生成器对象<br>
     * ThreadLocalRandom是JDK 7之后提供并发产生随机数，能够解决多个线程发生的竞争争夺。
     *
     * <p>
     * 注意：此方法返回的{@link ThreadLocalRandom}不可以在多线程环境下共享对象，否则有重复随机数问题。
     * 见：<a href="https://www.jianshu.com/p/89dfe990295c">...</a>
     * </p>
     *
     * @return {@link ThreadLocalRandom}
     */
    public static ThreadLocalRandom getRandom() {
        return ThreadLocalRandom.current();
    }

    /**
     * 获得一个只包含数字的字符串
     *
     * @param length 字符串的长度
     * @return 随机字符串
     */
    public static String randomNumbers(int length) {
        return randomString(BASE_NUMBER, length);
    }

    /**
     * 获得一个随机的字符串
     *
     * @param baseString 随机字符选取的样本
     * @param length     字符串的长度
     * @return 随机字符串
     */
    public static String randomString(String baseString, int length) {
        if (StringUtils.isBlank(baseString)) {
            return StringUtils.EMPTY;
        }
        if (length < NumberConstant.INTEGER_EIGHT) {
            length = NumberConstant.INTEGER_EIGHT;
        }

        final StringBuilder sb = new StringBuilder(length);
        int baseLength = baseString.length();
        for (int i = 0; i < length; i++) {
            int number = randomInt(baseLength);
            sb.append(baseString.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 获得指定范围内的随机数 [0,limit)
     *
     * @param limit 限制随机数的范围，不包括这个数
     * @return 随机数
     * @see Random#nextInt(int)
     */
    public static int randomInt(int limit) {
        return getRandom().nextInt(limit);
    }
}
