package com.insurance.kakao.insurance.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ContractStatusEnum {
	NORMAL("N", "정상계약"),
	WITHDRAW("W", "청약철회"),
	EXPIRE("E", "기간만료");

	@Getter
	private final String status;
	private final String desc;
}
