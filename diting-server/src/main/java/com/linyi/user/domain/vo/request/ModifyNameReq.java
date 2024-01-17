package com.linyi.user.domain.vo.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ModifyNameReq {

    @NotNull
    @Length(max = 6, message = "用户名可别取太长，不然我记不住噢")
    @ApiModelProperty("用户名")
    private String name;

}