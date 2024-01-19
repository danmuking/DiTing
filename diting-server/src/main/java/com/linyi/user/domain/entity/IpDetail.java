package com.linyi.user.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @program: DiTing
 * @description: 用户ip详情
 * @author: lin
 * @create: 2024-01-18 17:42
 **/

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IpDetail implements Serializable {
    private static final long serialVersionUID = 1L;
    //注册时的ip
    private String ip;
    //最新登录的ip
    private String isp;
    private String isp_id;
    private String city;
    private String city_id;
    private String country;
    private String country_id;
    private String region;
    private String region_id;
}
