package com.maoyou.security.service.impl;

import com.maoyou.security.entity.Userrole;
import com.maoyou.security.mapper.UserroleMapper;
import com.maoyou.security.service.UserroleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户角色关系表 服务实现类
 * </p>
 *
 * @author maoyou
 * @since 2022-07-07
 */
@Service
public class UserroleServiceImpl extends ServiceImpl<UserroleMapper, Userrole> implements UserroleService {

}
