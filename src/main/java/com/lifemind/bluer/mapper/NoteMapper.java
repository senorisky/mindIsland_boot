package com.lifemind.bluer.mapper;

import com.lifemind.bluer.entity.Note;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
public interface NoteMapper extends BaseMapper<Note> {

    List<String> selectNoteIdListByUid(@Param("userId") String userId);
}
