package com.insurance.kakao.insurance.service;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CacheService {
	private final CacheManager cacheManager;

	public void removeCacheByName(String cacheName) {
		Cache cache = cacheManager.getCache(cacheName);
		if (ObjectUtils.isEmpty(cache)) {
			return;
		}
		cache.invalidate();
	}
}
