package com.lifemind.bluer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lifemind.bluer.entity.Page;

/**
* @author senorisky
* @description 针对表【page】的数据库操作Service
* @createDate 2022-11-14 09:43:42
*/
public interface PageService extends IService<Page> {

    Page UpdateItemContent(String noteId, String text, Integer index);
}
