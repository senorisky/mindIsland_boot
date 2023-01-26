package com.lifemind.bluer.service;

import com.lifemind.bluer.entity.Gallery;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author ckz
 * @since 2022-11-06
 */
public interface IGalleryService extends IService<Gallery> {
    Gallery deleteOneGalleryPic(String viewId, String name);

    void downPic(String userId, String name, String viewId, HttpServletResponse response);
}
