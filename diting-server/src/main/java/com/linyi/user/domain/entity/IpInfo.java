package com.linyi.user.domain.entity;

import lombok.Data;
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
@Data
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

    /**
     * @param ip:
     * @return void
     * @description 刷新ip详情
     * @date 2024/1/19 17:08
     */
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
//                判断updateIpDetail的ip是否和updateIp相等，相等则不需要刷新
                .filter(ip -> Objects.equals(ip, updateIp))
                .isPresent();
        return notNeedRefresh ? null : updateIp;
    }

    /**
     * @param ipDetail: 查询到的ip详情
     * @return void
     * @description 刷新ip详情
     * @date 2024/1/19 17:04
     */
    public void refreshIpDetail(IpDetail ipDetail) {
//        如果createIp与ipDetail的ip相等，则更新createIpDetail
        if (Objects.equals(createIp, ipDetail.getIp())) {
            createIpDetail = ipDetail;
        }
//        如果updateIp与ipDetail的ip相等，则更新createIpDetail
        if (Objects.equals(updateIp, ipDetail.getIp())) {
            updateIpDetail = ipDetail;
        }
    }

}
