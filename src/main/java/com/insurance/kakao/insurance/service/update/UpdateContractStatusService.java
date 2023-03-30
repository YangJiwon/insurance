package com.insurance.kakao.insurance.service.update;

import org.springframework.stereotype.Service;

import com.insurance.kakao.insurance.common.exception.BusinessErrorCodeException;
import com.insurance.kakao.insurance.common.exception.ErrorCode;
import com.insurance.kakao.insurance.mapper.InsuranceCommandMapper;
import com.insurance.kakao.insurance.model.vo.UpdateContract;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
class UpdateContractStatusService implements InsuranceModifiable {
	private final InsuranceCommandMapper command;

	@Override
	public void update(UpdateContract updateContract) {
		if(command.updateContractStatus(updateContract.getContractNo(), updateContract.getContractStatusValue()) != 1){
			throw new BusinessErrorCodeException(ErrorCode.ERROR13);
		}
	}

	@Override
	public void validation(UpdateContract updateContract){

	}
}
