package com.lifemind.bluer.service.impl;

import com.lifemind.bluer.entity.Note;
import com.lifemind.bluer.mapper.NoteMapper;
import com.lifemind.bluer.service.INoteService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ckz
 * @since 2023-03-01
 */
@Service
public class NoteServiceImpl extends ServiceImpl<NoteMapper, Note> implements INoteService {

}
