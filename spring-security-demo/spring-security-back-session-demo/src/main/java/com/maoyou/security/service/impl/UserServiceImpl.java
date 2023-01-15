package com.maoyou.security.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.maoyou.security.entity.User;
import com.maoyou.security.mapper.UserMapper;
import com.maoyou.security.service.UserService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author maoyou
 * @since 2022-07-03
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
