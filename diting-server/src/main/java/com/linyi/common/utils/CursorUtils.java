package com.linyi.common.utils;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.linyi.common.domain.vo.request.CursorPageBaseReq;
import com.linyi.common.domain.vo.response.CursorPageBaseResp;

import java.util.Date;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * @program: DiTing
 * @description: 游标分页工具
 * @author: lin
 * @create: 2024-01-23 20:08
 **/
public class CursorUtils {
    public static <T> CursorPageBaseResp<T> getCursorPageByMysql(IService<T> mapper, CursorPageBaseReq request, Consumer<LambdaQueryWrapper<T>> initWrapper, SFunction<T, ?> cursorColumn){
//        游标字段类型
        Class<?> cursorType = LambdaUtils.getReturnType(cursorColumn);
        LambdaQueryWrapper<T> wrapper = new LambdaQueryWrapper<>();
//        组装条件
        initWrapper.accept(wrapper);
//        游标条件
        if (StrUtil.isNotBlank(request.getCursor())) {
//            添加游标条件
            wrapper.lt(cursorColumn, parseCursor(request.getCursor(), cursorType));
        }
//        游标方向
        wrapper.orderByDesc(cursorColumn);
        Page<T> page = mapper.page(request.plusPage(), wrapper);
//        取出游标
        String cursor = Optional.ofNullable(CollectionUtil.getLast(page.getRecords()))
//                获取最后一条记录的游标值
                .map(cursorColumn)
                .map(CursorUtils::toCursor)
                .orElse(null);
//        判断是否是最后一页
        Boolean isLast = page.getRecords().size() != request.getPageSize();
        return new CursorPageBaseResp<>(cursor, isLast, page.getRecords());
    }

    private static String toCursor(Object o) {
        if (o instanceof Date) {
            return String.valueOf(((Date) o).getTime());
        } else {
            return o.toString();
        }
    }

    private static Object parseCursor(String cursor, Class<?> cursorType) {
        if (Date.class.isAssignableFrom(cursorType)) {
            return new Date(Long.parseLong(cursor));
        } else {
            return cursor;
        }
    }
}
