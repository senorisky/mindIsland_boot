package com.lifemind.bluer.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lifemind.bluer.entity.Page;
import com.lifemind.bluer.service.PageService;
import com.lifemind.bluer.mapper.PageMapper;
import org.springframework.stereotype.Service;

/**
* @author senorisky
* @description 针对表【page】的数据库操作Service实现
* @createDate 2022-11-14 09:43:42
*/
@Service
public class PageServiceImpl extends ServiceImpl<PageMapper, Page>
implements PageService{

}
