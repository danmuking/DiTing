package com.linyi.chat.service;

import com.linyi.chat.domain.vo.request.AdminAddReq;
import com.linyi.chat.domain.vo.request.MemberExitReq;

/**
 * <p>
 * 群成员表 服务类
 * </p>
 *
 * @author lin
 * @since 2024-01-24
 */
public interface GroupMemberService {

    /**
     * @param uid:
     * @param request:
     * @return void
     * @description 退出群聊
     * @date 2024/1/29 18:57
     */
    void exitGroup(Long uid, MemberExitReq request);

    /**
     * @param uid:
     * @param request:
     * @return void
     * @description 添加管理员
     * @date 2024/1/29 19:52
     */
    void addAdmin(Long uid, AdminAddReq request);
}
