package com.linyi.user.service.impl;

import com.linyi.chat.domain.vo.response.IdRespVO;
import com.linyi.common.domain.vo.response.ApiResult;
import com.linyi.common.utils.AssertUtil;
import com.linyi.user.dao.UserEmojiDao;
import com.linyi.user.domain.entity.UserEmoji;
import com.linyi.user.domain.vo.request.user.UserEmojiReq;
import com.linyi.user.domain.vo.response.UserEmojiResp;
import com.linyi.user.service.UserEmojiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: DiTing
 * @description:
 * @author: lin
 * @create: 2024-02-08 22:13
 **/
@Service
public class UserEmojiServiceImpl implements UserEmojiService {
    public static final int MAX_EMOJI_COUNT = 30;
    @Autowired
    private UserEmojiDao userEmojiDao;
    /**
     * @param req :
     * @param uid :
     * @return ApiResult<IdRespVO>
     * @description 新增表情包
     * @date 2024/2/8 22:07
     */
    @Override
    public ApiResult<IdRespVO> insert(UserEmojiReq req, Long uid) {
//        检查表情包是否超过上限
        int count = userEmojiDao.countByUid(uid);
        AssertUtil.isFalse(count > MAX_EMOJI_COUNT, "最多只能添加30个表情哦~~");
//        校验表情是否存在
        Integer existsCount = userEmojiDao.lambdaQuery()
                .eq(UserEmoji::getExpressionUrl, req.getExpressionUrl())
                .eq(UserEmoji::getUid, uid)
                .count();
        AssertUtil.isFalse(existsCount > 0, "当前表情已存在哦~~");
        UserEmoji insert = UserEmoji.builder().uid(uid).expressionUrl(req.getExpressionUrl()).build();
        userEmojiDao.save(insert);
        return ApiResult.success(IdRespVO.id(insert.getId()));
    }

    @Override
    public List<UserEmojiResp> list(Long uid) {
        return userEmojiDao.listByUid(uid).
                stream()
                .map(a -> UserEmojiResp.builder()
                        .id(a.getId())
                        .expressionUrl(a.getExpressionUrl())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public void remove(long id, Long uid) {
//        检查表情包是否存在
        UserEmoji userEmoji = userEmojiDao.getById(id);
        AssertUtil.isNotEmpty(userEmoji, "表情包不存在");
//        检查表情包的拥有者
        AssertUtil.equal(userEmoji.getUid(), uid, "小黑子，别人表情不是你能删的");
//        删除表情包
        userEmojiDao.removeById(id);
    }
}
