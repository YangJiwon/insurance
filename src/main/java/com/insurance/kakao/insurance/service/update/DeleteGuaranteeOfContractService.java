package com.insurance.kakao.insurance.service.update;

import java.util.List;

import org.springframework.stereotype.Service;

import com.insurance.kakao.insurance.common.exception.BusinessErrorCodeException;
import com.insurance.kakao.insurance.common.exception.ErrorCode;
import com.insurance.kakao.insurance.mapper.InsuranceCommandMapper;
import com.insurance.kakao.insurance.model.vo.UpdateContract;
import com.insurance.kakao.insurance.service.InsuranceSelectService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
class DeleteGuaranteeOfContractService implements InsuranceModifiable {
	private final InsuranceCommandMapper command;
	private final InsuranceSelectService insuranceSelectService;

	@Override
	public void update(UpdateContract contract) {
		List<Integer> guaranteeNoList = contract.getGuaranteeNoList();
		if(command.deleteGuaranteeOfContract(contract.getContractNo(), guaranteeNoList) != guaranteeNoList.size()){
			throw new BusinessErrorCodeException(ErrorCode.DELETE_GUARANTEE_OF_CONTRACT);
		}
	}

	@Override
	public void validation(UpdateContract contract){
		List<Integer> contractGuaranteeMappingNoList = insuranceSelectService.selectContractGuaranteeMappingNoList(contract.getContractNo());
		int curSize = contractGuaranteeMappingNoList.size();
		int requestSize = contract.getGuaranteeNoSize();
		if(curSize <= requestSize){
			throw new BusinessErrorCodeException(ErrorCode.NOT_VALID_DELETE_REQUEST);
		}
	}
}
