package com.maoyou.security.service.impl;

import com.maoyou.security.entity.Role;
import com.maoyou.security.mapper.RoleMapper;
import com.maoyou.security.service.RoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 角色表 服务实现类
 * </p>
 *
 * @author maoyou
 * @since 2022-07-07
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

}
