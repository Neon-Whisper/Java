package com.neon.controller;


import com.neon.pojo.Emp;
import com.neon.pojo.EmpQueryParam;
import com.neon.pojo.PageResult;
import com.neon.pojo.Result;
import com.neon.service.EmpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequestMapping("/emps")
@RestController
public class EmpController {

    @Autowired
    private EmpService empService;

    // 分页查询
//    @GetMapping
//    public Result page(@RequestParam(defaultValue = "1") Integer page,
//                       @RequestParam(defaultValue = "10") Integer pageSize,
//                       String name, Integer gender,
//                       @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
//                       @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end)
//    {
//        log.info("查询员工信息, page={}, pageSize={},", page, pageSize);
//        PageResult<Emp> pageResult = empService.page(page, pageSize);
//        return Result.success(pageResult);
//    }

    // 分页条件查询
    @GetMapping
    public Result page(EmpQueryParam empQueryParam) {
        log.info("查询请求参数： {}", empQueryParam);
        PageResult<Emp> pageResult = empService.page(empQueryParam);
        return Result.success(pageResult);
    }

    // 新增员工
    @PostMapping
    public Result save(@RequestBody Emp emp){
        log.info("请求参数emp: {}", emp);
        empService.save(emp);
        return Result.success();
    }

    // 删除员工
    @DeleteMapping
    public Result delete(@RequestParam List<Integer> ids){
        log.info("删除员工, id: {}", ids);
        empService.deleteById(ids);
        return Result.success();
    }

    //按id查询员工
    @GetMapping("/{id}")
    public Result getInfo(@PathVariable Integer id){
        log.info("根据ID查询, id: {}", id);
        Emp emp = empService.getInfo(id);
        return Result.success(emp);
    }

    // 修改员工
    @PutMapping
    public Result update(@RequestBody Emp emp)
    {
        log.info("修改员工, emp: {}", emp);
        empService.update(emp);
        return Result.success();
    }
}
