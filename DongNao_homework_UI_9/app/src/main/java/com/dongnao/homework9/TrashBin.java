package com.dongnao.homework9;

/**
 * Created by KK on 2017/5/23.
 */

public interface TrashBin {

    /**
     * 打开垃圾桶
     */
    void open();

    /**
     * 关闭垃圾桶
     */
    void close();

    /**
     * 是否已经打开
     * @return
     *         true or false
     */
    boolean isOpened();
}
