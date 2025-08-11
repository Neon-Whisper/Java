package com.neon.service;

import com.neon.pojo.JobOption;

import java.util.List;
import java.util.Map;

public interface ReportService {
    //统计员工职位数据
    JobOption getEmpJobData();
    //统计员工性别数据
    List<Map> getEmpGenderData();
}
