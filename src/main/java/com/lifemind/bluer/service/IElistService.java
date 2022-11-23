package com.lifemind.bluer.service;

import com.lifemind.bluer.entity.Elist;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ckz
 * @since 2022-11-06
 */
public interface IElistService extends IService<Elist> {

    void downResource(String userId, String name, HttpServletResponse response);
}
