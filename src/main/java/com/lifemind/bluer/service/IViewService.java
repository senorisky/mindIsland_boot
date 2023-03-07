package com.lifemind.bluer.service;

import com.lifemind.bluer.entity.View;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.IOException;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author ckz
 * @since 2022-11-06
 */
public interface IViewService extends IService<View> {
    boolean InitView(View view);

    boolean removeViewData(View view, String userId) throws IOException;
}
