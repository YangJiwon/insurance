package com.insurance.kakao.insurance.service.insert;

import com.insurance.kakao.insurance.common.CommonUtil;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum CreateInsuranceServiceEnums {
	CONTRACT(CreateContractService.class.getSimpleName()),
	PRODUCT(CreateProductService.class.getSimpleName()),
	GUARANTEE(CreateGuaranteeService.class.getSimpleName());

	private final String name;

	public String getName() {
		return CommonUtil.firstWordToLowerCase(name);
	}
}
