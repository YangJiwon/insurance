package com.insurance.kakao.insurace.service.update;

import java.util.List;

import org.springframework.stereotype.Service;

import com.insurance.kakao.insurace.common.exception.BusinessErrorCodeException;
import com.insurance.kakao.insurace.common.exception.ErrorCode;
import com.insurance.kakao.insurace.mapper.InsuranceCommandMapper;
import com.insurance.kakao.insurace.model.response.GuaranteeResponse;
import com.insurance.kakao.insurace.model.vo.UpdateContract;
import com.insurance.kakao.insurace.service.InsuranceSelectService;

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
			throw new BusinessErrorCodeException(ErrorCode.ERROR10);
		}
	}

	@Override
	public void validation(UpdateContract updateContract){
		int contractNo = updateContract.getContractNo();
		List<Integer> guaranteeNoList = updateContract.getGuaranteeNoList();

		List<GuaranteeResponse> requestGuaranteeList = insuranceSelectService.selectGuaranteeList(guaranteeNoList);
		if(requestGuaranteeList.size() != guaranteeNoList.size()){
			throw new BusinessErrorCodeException(ErrorCode.ERROR19);
		}

		if(isOtherProduct(requestGuaranteeList, contractNo)){
			throw new BusinessErrorCodeException(ErrorCode.ERROR14);
		}

		if(isContainGuarantee(contractNo, guaranteeNoList)){
			throw new BusinessErrorCodeException(ErrorCode.ERROR15);
		}
	}

	private boolean isOtherProduct(List<GuaranteeResponse> requestGuaranteeList, int contractNo){
		int productNo = insuranceSelectService.getContractInfo(contractNo).getProductNo();
		return requestGuaranteeList.stream()
				.map(GuaranteeResponse::getProductNo)
				.anyMatch(v -> v != productNo);
	}

	private boolean isContainGuarantee(int contractNo, List<Integer> guaranteeNoList){
		List<GuaranteeResponse> curGuaranteeList = insuranceSelectService.selectGuaranteeList(contractNo);
		return curGuaranteeList.stream()
				.map(GuaranteeResponse::getGuaranteeNo)
				.anyMatch(guaranteeNoList::contains);
	}
}
