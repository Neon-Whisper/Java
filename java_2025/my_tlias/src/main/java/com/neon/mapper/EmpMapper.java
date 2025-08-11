package com.neon.mapper;

import com.neon.pojo.Emp;
import com.neon.pojo.EmpQueryParam;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface EmpMapper {
//    // 查询所有员工
//    @Select("select e.*, d.name  deptName from emp e left join dept d on e.dept_id = d.id " +
//            "order by e.update_time limit #{start}, #{pageSize}")
//    public List<Emp> list(Integer start, Integer pageSize);
//
//    //查询总记录数
//    @Select("select count(*) from emp e left join dept d on e.dept_id = d.id")
//    public Long count();



//    //使用pagehelper分页查询
//    @Select("select e.*, d.name deptName from emp e left join dept d on e.dept_id = d.id order by e.update_time ")
//    public List<Emp> list();

    //查询
    List<Emp> list(EmpQueryParam empQueryParam);

    //新增
    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into emp(username, name, gender, phone, job, salary, image, entry_date, dept_id, create_time, update_time) " +
            "values (#{username},#{name},#{gender},#{phone},#{job},#{salary},#{image},#{entryDate},#{deptId},#{createTime},#{updateTime})")
    void insert(Emp emp);

    //删除
    void deleteById(List<Integer> ids);

    //按id查询
    Emp getById(Integer id);
    //修改
    void updateById(Emp emp);
    //统计员工人数
    @MapKey("pos")
    List<Map<String, Object>> countEmpJobData();
    //统计性别数据
    @MapKey("name")
    List<Map> countEmpGenderData();

    //登录验证
    @Select("select * from emp where username = #{username} and password = #{password}")
    Emp getUsernameAndPassword(Emp emp);
}
