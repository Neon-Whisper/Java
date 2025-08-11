package com.neon.service;

import com.neon.pojo.Emp;
import com.neon.pojo.EmpQueryParam;
import com.neon.pojo.LoginInfo;
import com.neon.pojo.PageResult;

import java.util.List;

public interface EmpService {

    // 分页查询
    PageResult<Emp> page(EmpQueryParam empQueryParam);
    //  保存
    void save(Emp emp);
    // 删除
    void deleteById(List<Integer> ids);
    // 按id查询
    Emp getInfo(Integer id);

    // 修改
    void update(Emp emp);

    // 登录验证
    LoginInfo login(Emp emp);
}
