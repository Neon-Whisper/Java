package com.neon.service.impl;

import com.neon.mapper.DeptMapper;
import com.neon.pojo.Dept;
import com.neon.service.DeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DeptServiceImpl implements DeptService {
    // 注入mapper
    @Autowired
    private DeptMapper deptMapper;

    // 查询所有部门
    @Override
    public List<Dept> findAll() {

        return deptMapper.findAll();
    }

    // 删除部门
    @Override
    public void deleteById(Integer id)
    {
        deptMapper.deleteById(id);
    }

    // 新增部门
    @Override
    public void save(Dept dept)
    {
        //补全基础属性
        dept.setCreateTime(LocalDateTime.now());
        dept.setUpdateTime(LocalDateTime.now());
        //保存部门
        deptMapper.insert(dept);
    }

    // 根据id查询部门
    @Override
    public Dept getById(Integer id) {
        return deptMapper.getById(id);
    }

    @Override
    public void update(Dept dept) {
        //补全基础属性
        dept.setUpdateTime(LocalDateTime.now());
        //保存部门
        deptMapper.update(dept);
    }
}
