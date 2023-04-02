package com.insurance.kakao.insurance.service.insert;

import com.insurance.kakao.insurance.model.vo.CreateInsurance;

interface InsuranceCreatable {
	void create(CreateInsurance createInsurance);

	default void validation(CreateInsurance createInsurance){
	}
}
