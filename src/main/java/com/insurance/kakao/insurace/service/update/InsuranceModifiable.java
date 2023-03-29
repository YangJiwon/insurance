package com.insurance.kakao.insurace.service.update;

import com.insurance.kakao.insurace.model.vo.UpdateContract;

interface InsuranceModifiable {
	default void insuranceUpdate(UpdateContract updateContract){
		validation(updateContract);
		update(updateContract);
	}

	void validation(UpdateContract updateContract);

	void update(UpdateContract updateContract);
}
