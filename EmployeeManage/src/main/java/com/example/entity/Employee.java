package com.example.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * Employee implements Serializable将其进行序列化，从而可以放入Redis
 * @author chenzufeng
 * @date 2021-07-03
 */
@Data
@Accessors(chain = true)
public class Employee implements Serializable {
    private String id;
    private String name;
    private String profilePicturePath;
    private Double salary;
    private Integer age;
}
