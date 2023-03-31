package com.insurance.kakao.insurance.service.update;

import java.util.List;

import org.springframework.stereotype.Service;

import com.insurance.kakao.insurance.common.exception.BusinessErrorCodeException;
import com.insurance.kakao.insurance.common.exception.ErrorCode;
import com.insurance.kakao.insurance.mapper.InsuranceCommandMapper;
import com.insurance.kakao.insurance.model.response.GuaranteeResponse;
import com.insurance.kakao.insurance.model.vo.UpdateContract;
import com.insurance.kakao.insurance.service.InsuranceSelectService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
class InsertGuaranteeOfContractService implements InsuranceModifiable {
	private final InsuranceCommandMapper command;
	private final InsuranceSelectService insuranceSelectService;

	@Override
	public void update(UpdateContract updateContract) {
		List<Integer> guaranteeNoList = updateContract.getGuaranteeNoList();
		if(command.insertGuaranteeOfContract(updateContract.getContractNo(), guaranteeNoList) != guaranteeNoList.size()){
			throw new BusinessErrorCodeException(ErrorCode.INSERT_GUARANTEE_OF_CONTRACT);
		}
	}

	@Override
	public void validation(UpdateContract updateContract){
		int contractNo = updateContract.getContractNo();
		List<Integer> guaranteeNoList = updateContract.getGuaranteeNoList();

		int productNo = insuranceSelectService.getContractInfo(contractNo).getProductNo();
		long notExistGuaranteeCount = insuranceSelectService.getNotExistGuaranteeCount(productNo, guaranteeNoList);
		if(notExistGuaranteeCount > 0){
			throw new BusinessErrorCodeException(ErrorCode.NOT_VALID_GUARANTEE);
		}

		if(isContainGuarantee(contractNo, guaranteeNoList)){
			throw new BusinessErrorCodeException(ErrorCode.ALREADY_EXIST_GUARANTEE);
		}
	}

	private boolean isContainGuarantee(int contractNo, List<Integer> guaranteeNoList){
		List<GuaranteeResponse> curGuaranteeList = insuranceSelectService.selectGuaranteeList(contractNo);
		return curGuaranteeList.stream()
				.map(GuaranteeResponse::getGuaranteeNo)
				.anyMatch(guaranteeNoList::contains);
	}
}
