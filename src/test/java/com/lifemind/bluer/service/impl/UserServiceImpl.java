package com.lifemind.bluer.service.impl;

import com.lifemind.bluer.entity.User;
import com.lifemind.bluer.mapper.UserMapper;
import com.lifemind.bluer.service.IUserService;
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
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}
