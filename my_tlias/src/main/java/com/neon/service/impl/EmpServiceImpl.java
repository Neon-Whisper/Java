package com.neon.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.neon.mapper.EmpExprMapper;
import com.neon.mapper.EmpMapper;
import com.neon.pojo.*;
import com.neon.service.EmpLogService;
import com.neon.service.EmpService;
import com.neon.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmpServiceImpl implements EmpService {

    @Autowired
    private EmpMapper empMapper;

    @Autowired
    private EmpExprMapper empExprMapper;

    @Autowired
    private EmpLogService empLogService;

//    // 分页查询
//    @Override
//    public PageResult page(Integer page, Integer pageSize) {
//        // 总记录数
//        Long total =empMapper.count();
//        //起始记录索引
//        Integer start = (page-1)*pageSize;
//        //分页查询
//        List<Emp> rows = empMapper.list(start, pageSize);
//        // 封装到PageResult对象中并返回
//        return new PageResult(total, rows);
//    }

//    //使用pageHelper分页插件查询
//    @Override
//    public PageResult<Emp> page(EmpQueryParam empQueryParam)
//    {
//        // 设置分页
//        PageHelper.startPage(page, pageSize);
//
//        // 正常查询
//        List<Emp> rows = empMapper.list();
//        //强转为Page
//        Page <Emp> p = (Page<Emp>) rows;
//
//        // 封装到PageResult对象中并返回
//        return new PageResult<Emp>(p.getTotal(),p.getResult());
//    }

    //分页条件查询
    @Override
    public PageResult<Emp> page(EmpQueryParam empQueryParam) {
        PageHelper.startPage(empQueryParam.getPage(),  empQueryParam.getPageSize());

        List<Emp> rows = empMapper.list(empQueryParam);

        Page<Emp> p = (Page<Emp>) rows;
        return new PageResult<Emp>(p.getTotal(), p.getResult());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void save(Emp emp)
    {
        try {
            //1.补全基础属性
            emp.setCreateTime(LocalDateTime.now());
            emp.setUpdateTime(LocalDateTime.now());
            //2.保存员工基本信息
            empMapper.insert(emp);
            //3. 保存员工的工作经历信息 - 批量
            Integer empId = emp.getId();
            List<EmpExpr> exprList = emp.getExprList();
            if (!exprList.isEmpty()) {
                exprList.forEach(empExpr -> empExpr.setEmpId(empId));
                empExprMapper.insertBatch(exprList);
            }
        }
        finally {
            //记录操作日志
            EmpLog empLog = new EmpLog(null, LocalDateTime.now(), emp.toString());
            empLogService.insertLog(empLog);
        }
    }

    // 删除员工
    @Transactional
    @Override
    public void deleteById(List<Integer> ids) {
        empMapper.deleteById(ids);
        empExprMapper.deleteById(ids);
    }

    // 按id获取员工信息
    @Override
    public Emp getInfo(Integer id) {
        return empMapper.getById(id);
    }

    // 更新员工信息
    @Transactional
    @Override
    public void update(Emp emp) {
        //1. 根据ID更新员工基本信息
        emp.setUpdateTime(LocalDateTime.now());
        empMapper.updateById(emp);

        //2. 根据员工ID删除员工的工作经历信息 【删除老的】
        empExprMapper.deleteById(Arrays.asList(emp.getId()));

        //3. 新增员工的工作经历数据 【新增新的】
        Integer empId = emp.getId();
        List<EmpExpr> exprList = emp.getExprList();
        if(!CollectionUtils.isEmpty(exprList))
        {
            exprList.forEach(empExpr -> empExpr.setEmpId(empId));
            empExprMapper.insertBatch(exprList);
        }
    }

    // 登录验证
    @Override
    public LoginInfo login(Emp emp) {
        Emp empLogin = empMapper.getUsernameAndPassword(emp);
        if(empLogin != null){
            //为jwt做map
            Map<String,Object> dataMap = new HashMap<>();
            dataMap.put("id", empLogin.getId());
            dataMap.put("username", empLogin.getUsername());
            String jwt = JwtUtils.generateJwt(dataMap);

            LoginInfo loginInfo = new LoginInfo(empLogin.getId(), empLogin.getUsername(), empLogin.getName(), jwt);
            return loginInfo;
        }
        return null;
    }
}
