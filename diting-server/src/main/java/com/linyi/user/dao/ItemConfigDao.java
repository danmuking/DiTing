package com.linyi.user.dao;

import com.linyi.user.domain.entity.ItemConfig;
import com.linyi.user.mapper.ItemConfigMapper;
import com.linyi.user.service.IItemConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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

}
