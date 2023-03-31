package com.insurance.kakao.insurance.service.update;

import com.insurance.kakao.insurance.model.vo.UpdateContract;

interface InsuranceModifiable {
	default void insuranceUpdate(UpdateContract updateContract){
		validation(updateContract);
		update(updateContract);
	}

	void validation(UpdateContract updateContract);

	void update(UpdateContract updateContract);

	default boolean isUpdateOnlyDate(){
		return false;
	}
}
