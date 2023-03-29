package com.insurance.kakao.insurace.service.update;

import org.springframework.stereotype.Service;

import com.insurance.kakao.insurace.common.exception.BusinessErrorCodeException;
import com.insurance.kakao.insurace.common.exception.ErrorCode;
import com.insurance.kakao.insurace.mapper.InsuranceCommandMapper;
import com.insurance.kakao.insurace.model.enums.ContractStatusEnum;
import com.insurance.kakao.insurace.model.vo.UpdateContract;
import com.insurance.kakao.insurace.service.InsuranceSelectService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
class UpdateContractStatusService implements InsuranceModifiable {
	private final InsuranceCommandMapper command;
	private final InsuranceSelectService insuranceSelectService;

	@Override
	public void insuranceUpdate(UpdateContract updateContract) {
		if(command.updateContractStatus(updateContract.getContractNo(), updateContract.getContractStatus().getStatus()) != 1){
			throw new BusinessErrorCodeException(ErrorCode.ERROR13);
		}
	}

	@Override
	public void isNotUpdate(UpdateContract updateContract){
		String curContractStatus = insuranceSelectService.getContractInfo(updateContract.getContractNo()).getContractStatus();
		if(ContractStatusEnum.EXPIRE.getStatus().equals(curContractStatus)){
			throw new BusinessErrorCodeException(ErrorCode.ERROR17);
		}
	}
}
