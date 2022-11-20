package com.lifemind.bluer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lifemind.bluer.entity.Page;
import org.apache.ibatis.annotations.Mapper;

/**
* @author senorisky
* @description 针对表【page】的数据库操作Mapper
* @createDate 2022-11-14 09:43:42
* @Entity com.lifemind.bluer.entity.Page
*/
@Mapper
public interface PageMapper extends BaseMapper<Page> {


}
