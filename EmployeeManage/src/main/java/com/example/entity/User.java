package com.example.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author chenzufeng
 * @date 2021-07-01
 */
@Data
@Accessors(chain = true)
public class User {
    private String id;
    private String name;
    private String realName;
    private String password;
    private String sexual;
    private String status;
    private Date registerTime;
}
