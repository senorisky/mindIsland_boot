package com.lifemind.bluer.service;

import com.lifemind.bluer.entity.Note;
import com.baomidou.mybatisplus.extension.service.IService;

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
}
