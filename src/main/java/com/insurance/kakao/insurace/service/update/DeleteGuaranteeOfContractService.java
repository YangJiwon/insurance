package com.insurance.kakao.insurace.service.update;

import java.util.List;

import org.springframework.stereotype.Service;

import com.insurance.kakao.insurace.common.exception.BusinessErrorCodeException;
import com.insurance.kakao.insurace.common.exception.ErrorCode;
import com.insurance.kakao.insurace.mapper.InsuranceCommandMapper;
import com.insurance.kakao.insurace.model.vo.UpdateContract;
import com.insurance.kakao.insurace.service.InsuranceSelectService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
class DeleteGuaranteeOfContractService implements InsuranceModifiable {
	private final InsuranceCommandMapper command;
	private final InsuranceSelectService insuranceSelectService;

	@Override
	public void update(UpdateContract updateContract) {
		List<Integer> guaranteeNoList = updateContract.getGuaranteeNoList();
		if(command.deleteGuaranteeOfContract(updateContract.getContractNo(), guaranteeNoList) != guaranteeNoList.size()){
			throw new BusinessErrorCodeException(ErrorCode.ERROR9);
		}
	}

	@Override
	public void validation(UpdateContract updateContract){
		List<Integer> guaranteeNoList = insuranceSelectService.selectGuaranteeNoList(updateContract.getContractNo());
		int curSize = guaranteeNoList.size();
		int requestSize = updateContract.getGuaranteeNoList().size();
		if(curSize <= requestSize){
			throw new BusinessErrorCodeException(ErrorCode.ERROR16);
		}
	}
}
