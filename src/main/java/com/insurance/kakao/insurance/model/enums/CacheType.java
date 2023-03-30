package com.insurance.kakao.insurance.model.enums;

import com.insurance.kakao.insurance.common.CacheKeyConstants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CacheType {
	PRODUCT(CacheKeyConstants.PRODUCT, (60 * 24) * 60),
	GUARANTEE(CacheKeyConstants.GUARANTEE, (60 * 24) * 60);

	private final String cacheName;
	private final int ttl;
}
