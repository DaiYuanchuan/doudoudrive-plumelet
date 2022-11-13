package com.doudoudrive.constant;

/**
 * <p>基本常量配置</p>
 * <p>2022-11-09 00:56</p>
 *
 * @author Dan
 **/
public interface ConstantConfig {

    /**
     * 特殊符号
     */
    interface SpecialSymbols {
        String DOT = ".";
        String ASTERISK = "*";
        String OR = "||";
        String QUESTION_MARK = "?";
        String COMMA = ",";
        String AND = "&&";
        String AMPERSAND = "&";
        String EQUALS = "=";
        String PLUS_SIGN = "+";
        String TILDE = "~";
        String ENGLISH_COLON = ":";
        String COMMENT_SIGN = "#";
        String HYPHEN = "-";
        String UNDERLINE = "_";
        String CURLY_BRACES = "{}";
        String LEFT_BRACKET = "(";
        String RIGHT_BRACKET = ")";
        String ENTER_LINUX = "\n";
    }

    /**
     * es索引名称相关常量
     */
    interface IndexName {
        String SYS_LOGBACK = "sys_logback";
    }

    /**
     * 用于创建索引时指定索引的索引存储类型
     */
    interface StoreType {
        /**
         * 默认文件系统实现。这将根据操作环境选择最佳的实现，当前所有支持的系统上都是MMapFS，但可能会发生变化。
         */
        String FS = "fs";

        /**
         * SimpleFS类型是使用随机访问文件直接实现文件系统存储（映射到Lucene SimpleFsDirectory）。
         * 此实现的并发性能较差（多线程将成为瓶颈）。当您需要索引持久性时，通常最好使用NioFs。
         */
        String SIMPLE_FS = "simplefs";

        /**
         * NIOFS类型使用NIO在文件系统上存储碎片索引（映射到Lucene NIOFSDirectory）。
         * 它允许多个线程同时读取同一文件。由于SUN Java实现中存在错误，因此不建议在Windows上使用。
         */
        String NIO_FS = "niofs";

        /**
         * MMapFS类型通过将文件映射到内存（MMap）来在文件系统上存储碎片索引（映射到Lucene MMapDirectory）。
         * 内存映射占用了进程中虚拟内存地址空间的一部分，其大小等于要映射的文件的大小。在使用这个类之前，请确保您已经允许了足够的虚拟地址空间。
         */
        String MMAP_FS = "mmapfs";
    }

}
