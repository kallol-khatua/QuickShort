package com.quickshort.workspace.service.impl;

import com.quickshort.workspace.dto.WorkspaceMemberDto;
import com.quickshort.workspace.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class RedisWorkspaceMemberService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisWorkspaceMemberService.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    String cacheKey = "workspace_member_owner_and_member:";

    public List<WorkspaceMemberDto> findInCache(User user) {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();

        // Check if data is in cache
        return (List<WorkspaceMemberDto>) valueOperations.get(cacheKey + user.getId().toString());
    }

    public void addToCache(User user, List<WorkspaceMemberDto> workspaceMemberDtoList) {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();

        // Cache result with TTL (e.g., 10 minutes)
        valueOperations.set(cacheKey + user.getId().toString(), workspaceMemberDtoList, 40, TimeUnit.SECONDS);
    }
}
