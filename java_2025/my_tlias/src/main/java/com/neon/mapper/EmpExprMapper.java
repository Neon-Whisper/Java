package com.neon.mapper;

import com.neon.pojo.EmpExpr;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EmpExprMapper
{

    void insertBatch(List<EmpExpr> exprList);

    void deleteById(List<Integer> ids);
}
