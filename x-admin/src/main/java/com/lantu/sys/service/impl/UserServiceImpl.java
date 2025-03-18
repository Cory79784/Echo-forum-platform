package com.lantu.sys.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lantu.sys.entity.User;
import com.lantu.sys.mapper.UserMapper;
import com.lantu.sys.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author laocai
 * @since 2023-02-07
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String USER_CACHE_KEY = "user:";
    private static final String USER_INFO_CACHE_KEY = "user_info:";
    private static final long TOKEN_EXPIRE_TIME = 30; // 30分钟

    @Override
    @Transactional
    public Map<String, Object> login(User user) {
        // 使用缓存查询用户信息
        String cacheKey = USER_CACHE_KEY + user.getUsername();
        User loginUser = (User) redisTemplate.opsForValue().get(cacheKey);
        
        if (loginUser == null) {
            // 缓存未命中，从数据库查询
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getUsername, user.getUsername());
            loginUser = this.baseMapper.selectOne(wrapper);
            
            if (loginUser != null) {
                // 将用户信息存入缓存
                redisTemplate.opsForValue().set(cacheKey, loginUser, 1, TimeUnit.HOURS);
            }
        }

        if (loginUser != null && passwordEncoder.matches(user.getPassword(), loginUser.getPassword())) {
            String token = UUID.randomUUID().toString();
            String key = USER_CACHE_KEY + token;

            // 存入redis，设置过期时间
            loginUser.setPassword(null);
            redisTemplate.opsForValue().set(key, loginUser, TOKEN_EXPIRE_TIME, TimeUnit.MINUTES);

            Map<String, Object> data = new HashMap<>();
            data.put("token", key);
            return data;
        }
        return null;
    }

    @Override
    @Cacheable(value = "userInfo", key = "#token")
    public Map<String, Object> getUserInfo(String token) {
        Object obj = redisTemplate.opsForValue().get(token);
        if (obj != null) {
            User loginUser = JSON.parseObject(JSON.toJSONString(obj), User.class);
            Map<String, Object> data = new HashMap<>();
            data.put("name", loginUser.getUsername());
            data.put("avatar", loginUser.getAvatar());

            // 使用缓存查询角色信息
            String roleCacheKey = USER_INFO_CACHE_KEY + loginUser.getId();
            List<String> roleList = (List<String>) redisTemplate.opsForValue().get(roleCacheKey);
            
            if (roleList == null) {
                roleList = this.baseMapper.getRoleNameByUserId(loginUser.getId());
                redisTemplate.opsForValue().set(roleCacheKey, roleList, 1, TimeUnit.HOURS);
            }
            
            data.put("roles", roleList);
            return data;
        }
        return null;
    }

    @Override
    @Transactional
    @CacheEvict(value = "userInfo", key = "#token")
    public void logout(String token) {
        redisTemplate.delete(token);
    }

    @Override
    @Transactional
    @CacheEvict(value = "userInfo", key = "#user.id")
    public boolean updateById(User user) {
        // 更新用户信息时清除相关缓存
        String cacheKey = USER_CACHE_KEY + user.getUsername();
        redisTemplate.delete(cacheKey);
        return super.updateById(user);
    }
}
