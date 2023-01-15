package com.maoyou.security.service.impl;

import com.maoyou.security.entity.Resource;
import com.maoyou.security.mapper.ResourceMapper;
import com.maoyou.security.service.ResourceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 资源表 服务实现类
 * </p>
 *
 * @author maoyou
 * @since 2022-07-08
 */
@Service
public class ResourceServiceImpl extends ServiceImpl<ResourceMapper, Resource> implements ResourceService {

}
