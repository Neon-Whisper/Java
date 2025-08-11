package com.neon.controller;

import com.neon.pojo.Dept;
import com.neon.pojo.Result;
import com.neon.service.DeptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequestMapping("/depts")
@RestController
public class DeptController {
    // 注入service
    @Autowired
    private DeptService deptService;

    // 查询所有部门信息
    @GetMapping
    public Result list()
    {
        log.info("查询部门列表");
        List<Dept> deptList = deptService.findAll();
        return Result.success(deptList);
    }

    // 删除部门
    @DeleteMapping
    public Result delete (Integer id)
    {
        log.info("根据id删除部门, id: {}",id );
        deptService.deleteById(id);
        return Result.success();
    }

    // 新增部门
    @PostMapping
    public Result save(@RequestBody Dept dept)
    {
        log.info("新增部门, dept: {}", dept);
        deptService.save(dept);
        return Result.success();
    }

    //根据id查询部门
    @GetMapping("/{id}")
    public Result getById(@PathVariable Integer id)
    {
        log.info("根据ID查询, id: {}", id);
        Dept dept = deptService.getById(id);
        return Result.success(dept);
    }

    // 修改部门
    @PutMapping
    public Result update(@RequestBody Dept dept)
    {
        log.info("修改部门, dept: {}", dept);
        deptService.update(dept);
        return Result.success();
    }

}
