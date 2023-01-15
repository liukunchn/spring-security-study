package com.maoyou.security.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.maoyou.security.entity.Url;
import com.maoyou.security.mapper.UrlMapper;
import com.maoyou.security.service.UrlService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * URL表 服务实现类
 * </p>
 *
 * @author maoyou
 * @since 2022-07-08
 */
@Service
public class UrlServiceImpl extends ServiceImpl<UrlMapper, Url> implements UrlService {

}
