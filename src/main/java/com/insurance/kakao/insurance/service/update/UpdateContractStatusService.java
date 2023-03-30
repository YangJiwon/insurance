package com.insurance.kakao.insurance.service.update;

import org.springframework.stereotype.Service;

import com.insurance.kakao.insurance.common.exception.BusinessErrorCodeException;
import com.insurance.kakao.insurance.common.exception.ErrorCode;
import com.insurance.kakao.insurance.mapper.InsuranceCommandMapper;
import com.insurance.kakao.insurance.model.enums.ContractStatusEnum;
import com.insurance.kakao.insurance.model.vo.UpdateContract;
import com.insurance.kakao.insurance.service.InsuranceSelectService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
class UpdateContractStatusService implements InsuranceModifiable {
	private final InsuranceCommandMapper command;
	private final InsuranceSelectService insuranceSelectService;

	@Override
	public void update(UpdateContract updateContract) {
		if(command.updateContractStatus(updateContract.getContractNo(), updateContract.getContractStatusValue()) != 1){
			throw new BusinessErrorCodeException(ErrorCode.ERROR13);
		}
	}

	@Override
	public void validation(UpdateContract updateContract){
		String curContractStatus = insuranceSelectService.getContractInfo(updateContract.getContractNo()).getContractStatus();
		if(ContractStatusEnum.EXPIRE.getStatus().equals(curContractStatus)){
			throw new BusinessErrorCodeException(ErrorCode.ERROR17);
		}
	}
}
