package com.insurance.kakao.insurance.common.config;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.insurance.kakao.insurance.model.enums.CacheType;

@Configuration
public class CacheConfig {
	@Bean
	public List<CaffeineCache> caffeineConfig() {
		return Arrays.stream(CacheType.values())
				.map(v -> new CaffeineCache(v.getCacheName(),
						Caffeine.newBuilder()
								.recordStats()
								.expireAfterWrite(v.getTtl(), TimeUnit.SECONDS)
								.build()
						)
				).collect(Collectors.toList());
	}

	@Bean
	public CacheManager cacheManager(List<CaffeineCache> caffeineCaches) {
		final SimpleCacheManager cacheManager = new SimpleCacheManager();
		cacheManager.setCaches(caffeineCaches);
		return cacheManager;
	}
}
