package com.lifemind.bluer.mapper;

import com.lifemind.bluer.entity.Elist;
import com.lifemind.bluer.entity.Etable;
import com.lifemind.bluer.entity.Note;
import com.lifemind.bluer.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

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
public interface UserMapper extends BaseMapper<User> {

}
