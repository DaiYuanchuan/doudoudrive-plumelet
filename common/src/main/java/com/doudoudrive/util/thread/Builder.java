package com.doudoudrive.util.thread;

import java.io.Serializable;

/**
 * <p>建造者模式接口定义</p>
 * <p>2022-06-07 12:33</p>
 *
 * @author Dan
 **/
@FunctionalInterface
public interface Builder<T> extends Serializable {

    /**
     * 构建
     *
     * @return 被构建的对象
     */
    T build();

}