package com.lifemind.bluer.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lifemind.bluer.entity.View;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.baomidou.mybatisplus.core.toolkit.Constants;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author ckz
 * @since 2022-11-06
 */
@Mapper
public interface ViewMapper extends BaseMapper<View> {

    @Select("select v.id from view v ${ew.customSqlSegment}")
    List<String> selectViewIdListByNids(@Param(Constants.WRAPPER) QueryWrapper wrapper);

    List<String> selectViewIdByNid(@Param("nid") String nid);
}
