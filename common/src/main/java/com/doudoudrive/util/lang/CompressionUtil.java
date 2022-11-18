package com.doudoudrive.util.lang;

import com.doudoudrive.constant.NumberConstant;
import com.github.luben.zstd.Zstd;

/**
 * <p>集成压缩算法工具类</p>
 * <p>2022-11-11 12:12</p>
 *
 * @author Dan
 **/
public class CompressionUtil {

    /**
     * 字节解压缩为字节数组，解压失败返回一个空的字节数组
     *
     * @param bytes 字节数组
     * @return 解压后的字节数组
     */
    public static byte[] decompressBytes(byte[] bytes) {
        if (CollectionUtil.isEmpty(bytes)) {
            return new byte[NumberConstant.INTEGER_ZERO];
        }
        int size = (int) Zstd.decompressedSize(bytes);
        if (size == NumberConstant.INTEGER_ZERO) {
            return new byte[NumberConstant.INTEGER_ZERO];
        }
        byte[] ob = new byte[size];
        Zstd.decompress(ob, bytes);
        return ob;
    }
}