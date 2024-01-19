package com.linyi.user.domain.entity;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

/**
 * @program: DiTing
 * @description: 用户ip信息
 * @author: lin
 * @create: 2024-01-18 17:41
 **/
public class IpInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    //注册时的ip
    private String createIp;
    //注册时的ip详情
    private IpDetail createIpDetail;
    //最新登录的ip
    private String updateIp;
    //最新登录的ip详情
    private IpDetail updateIpDetail;

    public void refreshIp(String ip) {
        if (StringUtils.isEmpty(ip)) {
            return;
        }
        updateIp = ip;
        if (createIp == null) {
            createIp = ip;
        }
    }

    /**
     * @param :
     * @return String
     * @description 是否要刷新ip
     * @date 2024/1/18 18:17
     */
    public String needRefreshIp() {
//        updateIp为空，不需要刷新
        boolean notNeedRefresh = Optional.ofNullable(updateIpDetail)
                .map(IpDetail::getIp)
                .filter(ip -> Objects.equals(ip, updateIp))
                .isPresent();
        return notNeedRefresh ? null : updateIp;
    }

    public void refreshIpDetail(IpDetail ipDetail) {
        if (Objects.equals(createIp, ipDetail.getIp())) {
            createIpDetail = ipDetail;
        }
        if (Objects.equals(updateIp, ipDetail.getIp())) {
            updateIpDetail = ipDetail;
        }
    }

}
