package com.lifemind.bluer.service;

import com.lifemind.bluer.entity.Etable;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author ckz
 * @since 2022-11-06
 */
public interface IEtableService extends IService<Etable> {
    Etable addColum(String name, String viewId);

    Etable deleteColum(String name, String viewId);

    Etable addItem( String viewId);

    Etable deleteItem(Integer index, String viewId);
}
