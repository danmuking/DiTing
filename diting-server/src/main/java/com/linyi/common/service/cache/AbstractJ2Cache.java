package com.linyi.common.service.cache;

import cn.hutool.core.collection.CollectionUtil;
import com.linyi.common.utils.RedisUtils;
import net.oschina.j2cache.CacheChannel;
import net.oschina.j2cache.CacheObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;

import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.stream.Collectors;

import static com.linyi.common.constant.J2CacheConstant.REGION;

/**
 * @program: DiTing
 * @description: J2Cache缓存框架
 * @author: lin
 * @create: 2024-02-06 20:03
 **/
public abstract class AbstractJ2Cache<IN,OUT> implements BatchCache<IN,OUT>{
    @Autowired
    private CacheChannel cacheChannel;
    private Class<OUT> outClass;

    protected AbstractJ2Cache() {
        ParameterizedType genericSuperclass = (ParameterizedType) this.getClass().getGenericSuperclass();
        this.outClass = (Class<OUT>) genericSuperclass.getActualTypeArguments()[1];
    }

    protected abstract String getKey(IN req);

    protected abstract Map<IN, OUT> load(List<IN> req);

    @Override
    public OUT get(IN req) {
        return getBatch(Collections.singletonList(req)).get(req);
    }

    @Override
    public Map<IN, OUT> getBatch(List<IN> req) {
        if (CollectionUtil.isEmpty(req)) {//防御性编程
            return new HashMap<>();
        }
        //去重
        req = req.stream().distinct().collect(Collectors.toList());

        //组装key
        List<String> keys = req.stream().map(this::getKey).collect(Collectors.toList());
        //批量get
        Map<String, CacheObject> stringCacheObjectMap = cacheChannel.get(REGION, keys);
        List<OUT> valueList = stringCacheObjectMap.values().stream().map(a -> Objects.nonNull(a.getValue())?(OUT) a.getValue():null).collect(Collectors.toList());
        //差集计算
        List<IN> loadReqs = new ArrayList<>();
        for (int i = 0; i < valueList.size(); i++) {
            if (Objects.isNull(valueList.get(i))) {
                loadReqs.add(req.get(i));
            }
        }
        Map<IN, OUT> load = new HashMap<>();
        //不足的重新加载进缓存
        if (CollectionUtil.isNotEmpty(loadReqs)) {
            //批量load
            load = load(loadReqs);
            Map<String, OUT> loadMap = load.entrySet().stream()
                    .map(a -> Pair.of(getKey(a.getKey()), a.getValue()))
                    .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
            cacheChannel.set(REGION, (Map<String, Object>) loadMap);
        }

        //组装最后的结果
        Map<IN, OUT> resultMap = new HashMap<>();
        for (int i = 0; i < req.size(); i++) {
            IN in = req.get(i);
            OUT out = Optional.ofNullable(valueList.get(i))
                    .orElse(load.get(in));
            resultMap.put(in, out);
        }
        return resultMap;
    }

    @Override
    public void delete(IN req) {
        deleteBatch(Collections.singletonList(req));
    }

    @Override
    public void deleteBatch(List<IN> req) {
        List<String> keys = req.stream().map(this::getKey).collect(Collectors.toList());
        for (String key : keys) {
            cacheChannel.evict(REGION, key);
        }
    }
}
