package com.example.service;

import com.example.entity.Employee;

import java.util.List;

/**
 * @author chenzufeng
 * @date 2021-07-03
 */
public interface EmployeeService {

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
     * 删除某一id的员工
     * @param id 被删除员工的id
     */
    void delete(String id);

    /**
     * 根据id查找员工
     * @param id 待查找员工的id
     * @return 被找到的员工
     */
    Employee findEmployee(String id);

    /**
     * 更新员工信息
     * @param employee 待更新信息的员工
     */
    void update(Employee employee);
}
