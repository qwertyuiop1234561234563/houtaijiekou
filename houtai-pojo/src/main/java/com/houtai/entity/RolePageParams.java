package com.houtai.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RolePageParams extends PageParams {
    private String name;
    private String code;
}
