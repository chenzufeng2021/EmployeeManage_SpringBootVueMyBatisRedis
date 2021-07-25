package com.example.controller;

import com.example.entity.Employee;
import com.example.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author chenzufeng
 * @date 2021-07-03
 */
@RestController
@RequestMapping("Employee")
@CrossOrigin
@Slf4j
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @Value("${profilePictures.dir}")
    private String path;

    @PostMapping("AddEmployee")
    public Map<String, Object> addEmployee(Employee employee, MultipartFile photo) {
        log.info("员工信息：{}", employee.toString());
        log.info("员工头像信息：{}", photo.getOriginalFilename());

        Map<String, Object> map = new HashMap<>(2);

        try {
            // 将员工头像保存至本地
            String newFileName = UUID.randomUUID().toString() + "."
                    + FilenameUtils.getExtension(photo.getOriginalFilename());
            photo.transferTo(new File(path, newFileName));
            // 设置头像地址
            employee.setProfilePicturePath(newFileName);
            // 添加员工
            employeeService.addEmployee(employee);
            map.put("state", true);
            map.put("message", "员工信息保存成功！");
        } catch (Exception e) {
            e.printStackTrace();
            map.put("state",false);
            map.put("message", "员工信息保存失败！请重新添加！");
        }
        return map;
    }

    /**
     * 获取所有员工信息
     * @return 员工列表
     */
    @GetMapping("EmployeeList")
    public List<Employee> findAllEmployee() {
        return employeeService.findAll();
    }

    /**
     * 根据id查找员工
     * @param id 被查找员工的id
     * @return 被查询的员工
     */
    @GetMapping("FindEmployeeById")
    public Employee findEmployee(String id) {
        log.info("被查询员工的id：{}", id);
        return employeeService.findEmployee(id);
    }

    /**
     * 根据id删除员工
     * @param id 删除员工的id
     * @return 删除状态信息
     */
    @GetMapping("DeleteEmployee")
    public Map<String, Object> deleteEmployee(String id) {
        log.info("删除员工的id：{}", id);
        Map<String, Object> map = new HashMap<>(2);
        try {
            // 删除员工头像
            Employee employee = employeeService.findEmployee(id);
            File file = new File(path, employee.getProfilePicturePath());
            if (file.exists()) {
                file.delete();
            }
            // 删除员工信息
            employeeService.delete(id);
            map.put("state", true);
            map.put("message", "删除员工成功！");
        } catch (Exception exception) {
            exception.printStackTrace();
            map.put("state", false);
            map.put("message", "删除员工失败！");
        }
        return map;
    }

    /**
     * 跟新员工信息
     * @param employee 待更新信息的员工
     * @param photo 头像
     * @return 员工信息更新状态信息
     */
    @PostMapping("Update")
    public Map<String, Object> update(Employee employee, MultipartFile photo) {
        log.info("员工信息：{}", employee.toString());

        Map<String, Object> map = new HashMap<>(2);

        try {

            if (photo != null && photo.getSize() != 0) {
                log.info("员工头像信息：{}", photo.getOriginalFilename());
                // 保存修改后的头像
                String newFileName = UUID.randomUUID().toString() + "."
                        + FilenameUtils.getExtension(photo.getOriginalFilename());
                photo.transferTo(new File(path, newFileName));
                // 设置头像地址
                employee.setProfilePicturePath(newFileName);
            }

            // 保存员工更新后的信息
            employeeService.update(employee);
            map.put("state", true);
            map.put("message", "员工信息更新成功！");

        } catch (Exception exception) {
            exception.printStackTrace();
            map.put("state", false);
            map.put("message", "员工信息更新失败！");
        }

        return map;
    }
}
