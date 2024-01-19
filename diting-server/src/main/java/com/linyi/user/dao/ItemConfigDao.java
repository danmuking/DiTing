package com.linyi.user.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linyi.user.domain.entity.ItemConfig;
import com.linyi.user.mapper.ItemConfigMapper;
import com.linyi.user.service.IItemConfigService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 功能物品配置表 服务实现类
 * </p>
 *
 * @author lin
 * @since 2024-01-16
 */
@Service
public class ItemConfigDao extends ServiceImpl<ItemConfigMapper, ItemConfig> implements IItemConfigService {

    public List<ItemConfig> getByType(Integer itemType) {
        List<ItemConfig> list = lambdaQuery()
                .eq(ItemConfig::getType, itemType)
                .list();
        return list;
    }
}
