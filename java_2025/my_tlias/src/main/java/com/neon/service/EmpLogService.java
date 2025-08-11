package com.neon.service;

import com.neon.pojo.EmpLog;

public interface EmpLogService {
    //记录新增员工日志
    public void insertLog(EmpLog empLog);
}