package com.linyi.common.service.cache;

import java.util.List;
import java.util.Map;

/**
 * @program: DiTing
 * @description: 批量缓存
 * @author: lin
 * @create: 2024-01-30 19:43
 **/
public interface BatchCache<IN, OUT> {
    /**
     * 获取单个
     */
    OUT get(IN req);

    /**
     * 获取批量
     */
    Map<IN, OUT> getBatch(List<IN> req);

    /**
     * 修改删除单个
     */
    void delete(IN req);

    /**
     * 修改删除多个
     */
    void deleteBatch(List<IN> req);
}

