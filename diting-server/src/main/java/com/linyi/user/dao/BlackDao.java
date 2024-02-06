package com.linyi.user.dao;

import com.linyi.user.domain.entity.Black;
import com.linyi.user.mapper.BlackMapper;
import com.linyi.user.service.IBlackService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 黑名单 服务实现类
 * </p>
 *
 * @author lin
 * @since 2024-02-06
 */
@Service
public class BlackDao extends ServiceImpl<BlackMapper, Black> implements IBlackService {

}
