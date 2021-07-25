package com.example.service;

import com.example.dao.EmployeeDao;
import com.example.entity.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author chenzufeng
 * @date 2021-07-03
 * 处理业务逻辑、控制事务、调用Dao
 */
@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {
    @Autowired
    private EmployeeDao employeeDao;

    @Override
    public void addEmployee(Employee employee) {
        employeeDao.addEmployee(employee);
    }

    /**
     * 查询所有员工
     * @return 员工列表
     */
    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<Employee> findAll() {
        return employeeDao.findAll();
    }

    /**
     * 删除员工
     * @param id 被删除员工的id
     */
    @Override
    public void delete(String id) {
        employeeDao.delete(id);
    }

    /**
     * 根据id查找员工
     * @param id 待查找员工的id
     * @return 被找到的员工
     */
    @Override
    public Employee findEmployee(String id) {
        return employeeDao.findEmployee(id);
    }

    /**
     * 更新员工信息
     * @param employee 待更新信息的员工
     */
    @Override
    public void update(Employee employee) {
        employeeDao.update(employee);
    }
}
