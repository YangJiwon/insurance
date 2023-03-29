package com.insurance.kakao.insurace.service.update;

import com.insurance.kakao.insurace.model.vo.UpdateContract;

public interface InsuranceModifiable {
	void insuranceUpdate(UpdateContract updateContract);

	default void isNotUpdate(UpdateContract updateContract){
		// nothing
	}
}
