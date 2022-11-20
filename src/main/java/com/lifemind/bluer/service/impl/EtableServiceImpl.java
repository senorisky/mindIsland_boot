package com.lifemind.bluer.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.lifemind.bluer.entity.Etable;
import com.lifemind.bluer.mapper.EtableMapper;
import com.lifemind.bluer.service.IEtableService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author ckz
 * @since 2022-11-06
 */
@Service
public class EtableServiceImpl extends ServiceImpl<EtableMapper, Etable> implements IEtableService {

    @Autowired
    private EtableMapper etableMapper;

    private Etable addTableColumn(Etable etable, String name) {
        List<String> colums = JSON.parseArray(etable.getColum(), String.class);
        List<JSONObject> datas = JSON.parseArray(etable.getData(), JSONObject.class);
        colums.add(name);
        etable.setColums(colums);
        etable.setColum(JSON.toJSONString(colums));
        for (int i = 0; i < datas.size(); i++) {
            datas.get(i).put(name, "");
        }
        etable.setData(JSON.toJSONString(datas));
        etable.setDatas(datas);
        return etable;
    }

    private Etable addTableItem(Etable etable) {
        List<String> colums = JSON.parseArray(etable.getColum(), String.class);
        List<JSONObject> datas = JSON.parseArray(etable.getData(), JSONObject.class);
        etable.setColums(colums);
        Set<String> keys = datas.get(0).keySet();
        JSONObject a = new JSONObject();
        for (String key : keys) {
            a.put(key, "");
        }
        datas.add(a);
        etable.setData(JSON.toJSONString(datas));
        etable.setDatas(datas);
        return etable;
    }

    private Etable deleteTableColumn(Etable etable, String name) {
        List<String> colums = JSON.parseArray(etable.getColum(), String.class);
        List<JSONObject> datas = JSON.parseArray(etable.getData(), JSONObject.class);
        colums.remove(name);
        etable.setColums(colums);
        etable.setColum(JSON.toJSONString(colums));
        for (int i = 0; i < datas.size(); i++) {
            datas.get(i).remove(name);
        }
        etable.setData(JSON.toJSONString(datas));
        etable.setDatas(datas);
        return etable;
    }

    private Etable deleteTableItem(Etable etable, Integer index) {
        List<String> colums = JSON.parseArray(etable.getColum(), String.class);
        List<JSONObject> datas = JSON.parseArray(etable.getData(), JSONObject.class);
        datas.remove(index.intValue());
        etable.setData(JSON.toJSONString(datas));
        etable.setColums(colums);
        etable.setDatas(datas);
        return etable;
    }

    @Override
    public Etable addColum(String name, String viewId) {
        UpdateWrapper wrapper = new UpdateWrapper();
        wrapper.eq("view_id", viewId);
        Etable one = etableMapper.selectOne(wrapper);
        if (one == null) {
            return null;
        }
        one = addTableColumn(one, name);
        int update = etableMapper.update(one, wrapper);
        if (update == 1) {
            return one;
        }
        return null;
    }

    @Override
    public Etable deleteColum(String name, String viewId) {
        UpdateWrapper wrapper = new UpdateWrapper();
        wrapper.eq("view_id", viewId);
        Etable one = etableMapper.selectOne(wrapper);
        if (one == null) {
            return null;
        }
        one = deleteTableColumn(one, name);
        int update = etableMapper.update(one, wrapper);
        if (update == 1) {
            return one;
        }
        return null;
    }

    @Override
    public Etable addItem(String viewId) {
        UpdateWrapper wrapper = new UpdateWrapper();
        wrapper.eq("view_id", viewId);
        Etable one = etableMapper.selectOne(wrapper);
        if (one == null) {
            return null;
        }
        one = addTableItem(one);
        int update = etableMapper.update(one, wrapper);
        if (update == 1) {
            return one;
        }
        return null;
    }

    @Override
    public Etable deleteItem(Integer index, String viewId) {
        UpdateWrapper wrapper = new UpdateWrapper();
        wrapper.eq("view_id", viewId);
        Etable one = etableMapper.selectOne(wrapper);
        if (one == null) {
            return null;
        }
        one = deleteTableItem(one, index);
        int update = etableMapper.update(one, wrapper);
        if (update == 1) {
            return one;
        }
        return null;
    }
}
