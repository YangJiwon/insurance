package com.insurance.kakao.insurance.service.update;

import org.springframework.stereotype.Service;

import com.insurance.kakao.insurance.common.exception.BusinessErrorCodeException;
import com.insurance.kakao.insurance.common.exception.ErrorCode;
import com.insurance.kakao.insurance.mapper.InsuranceCommandMapper;
import com.insurance.kakao.insurance.model.response.ContractResponse;
import com.insurance.kakao.insurance.model.vo.UpdateContract;
import com.insurance.kakao.insurance.service.InsuranceSelectService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
class UpdateContractStatusService implements InsuranceModifiable {
	private final InsuranceCommandMapper command;
	private final InsuranceSelectService insuranceSelectService;

	@Override
	public void update(UpdateContract contract) {
		if(command.updateContractStatus(contract.getContractNo(), contract.getContractStatusValue()) != 1){
			throw new BusinessErrorCodeException(ErrorCode.UPDATE_CONTRACT_STATUS);
		}
	}

	@Override
	public void validation(UpdateContract contract){
		int contractNo = contract.getContractNo();
		ContractResponse contractResponse = insuranceSelectService.getContractInfo(contractNo);
		String curContractStatus = contractResponse.getContractStatus();
		if(curContractStatus.equals(contract.getContractStatusValue())){
			throw new BusinessErrorCodeException(ErrorCode.SAME_CONTRACT_STATUS);
		}
	}

	@Override
	public boolean isUpdateOnlyDate(){
		return true;
	}
}
