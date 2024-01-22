package com.linyi.user.dao;

import com.linyi.user.domain.entity.Room;
import com.linyi.user.mapper.RoomMapper;
import com.linyi.user.service.RoomService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 房间表 服务实现类
 * </p>
 *
 * @author lin
 * @since 2024-01-22
 */
@Service
public class RoomDao extends ServiceImpl<RoomMapper, Room> {

}
