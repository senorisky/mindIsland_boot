package com.lifemind.bluer.service;

import com.lifemind.bluer.entity.Note;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.IOException;
import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author ckz
 * @since 2022-11-06
 */
public interface INoteService extends IService<Note> {
    boolean InitDefaultPage(String nid);

    boolean removeNote(String note_id, String userId) throws IOException;

    boolean removePage(String note_id, String userId) throws IOException;

    List<Note> getMenuData(String user_id);
}
