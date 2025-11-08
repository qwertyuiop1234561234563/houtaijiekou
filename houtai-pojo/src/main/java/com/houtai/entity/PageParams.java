package com.houtai.entity;


import lombok.Data;

@Data
public class PageParams {
    private Integer page = 1;
    private Integer pageSize = 10;
    private String username;
    private String name;
    private String code;
}
