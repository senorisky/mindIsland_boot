package com.lifemind.bluer.service;

import com.lifemind.bluer.entity.Dto.MenuData;
import com.lifemind.bluer.entity.Note;
import com.lifemind.bluer.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author ckz
 * @since 2022-11-06
 */
public interface IUserService extends IService<User> {
    boolean checkExist(String email);

    boolean regist(User user);

    List<Note> getMenuData(String uid);

    boolean deleteUser(String userId);

    List<String> getUserPermissions(String principal);
}
