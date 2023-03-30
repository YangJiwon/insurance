package com.insurance.kakao.insurance.service.update;

import com.insurance.kakao.insurance.common.CommonUtil;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum UpdateContractServiceEnums {
	DELETE_GUARANTEE(DeleteGuaranteeOfContractService.class.getSimpleName()),
	INSERT_GUARANTEE(InsertGuaranteeOfContractService.class.getSimpleName()),
	UPDATE_PERIOD(UpdateContractPeriodService.class.getSimpleName()),
	UPDATE_STATUS(UpdateContractStatusService.class.getSimpleName());

	private final String name;

	public String getName() {
		return CommonUtil.firstWordToLowerCase(name);
	}
}
