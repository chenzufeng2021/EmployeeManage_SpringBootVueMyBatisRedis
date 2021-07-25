package com.example.dao;

import com.example.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author chenzufeng
 * @date 2021-07-03
 */
@Mapper
public interface EmployeeDao {
    /**
     * 添加员工
     * @param employee 员工
     */
    void addEmployee(Employee employee);

    /**
     * 查询所有员工
     * @return 员工列表
     */
    List<Employee> findAll();

    /**
     * 根据ID查找员工
     * @param id 待查找的员工的id
     * @return 被查询的员工
     */
    Employee findEmployee(String id);

    /**
     * 删除某一id的员工
     * @param id 被删除员工的id
     */
    void delete(String id);

    /**
     * 更新用户信息
     * @param employee 待更新信息的用户
     */
    void update(Employee employee);
}
