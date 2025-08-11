package com.neon.service;

import com.neon.pojo.Dept;

import java.util.List;

public interface DeptService {

    // 查询所有部门
    List<Dept> findAll();
    // 删除部门
    void deleteById(Integer id);
    // 添加部门
    void save(Dept dept);
    // 根据id查询部门
    Dept getById(Integer id);
    // 修改部门
    void update(Dept dept);
}
