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
    @Insert("insert  into user(user_id,email,password,user_name,create_Time,login_Time) " +
            "values(#{user.userId},#{user.email},#{user.password},#{user.userName},#{user.createTime},#{user.loginTime})")
    int insertUser(@Param("user") User user);

    @Select("select * from note where note.user_id=#{uid}")
    List<Note> selectNotes(String uid);

    @Select("select * from etable e where e.note_id=#{nid}")
    List<Etable> selectTable(String nid);

    @Select("select * from elist e where e.note_id=#{nid}")
    List<Elist> selectElist(String nid);

    @Select("select * from media_table e where e.note_id=#{nid}")
    List<Elist> selectMediaTable(String nid);
}
