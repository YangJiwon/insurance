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
	public void insuranceUpdate(UpdateContract updateContract) {
		if(command.insertGuaranteeOfContract(updateContract.getContractNo(), updateContract.getGuaranteeNoList()) != 1){
			throw new BusinessErrorCodeException(ErrorCode.ERROR10);
		}
	}

	@Override
	public void isNotUpdate(UpdateContract updateContract){
		int contractNo = updateContract.getContractNo();
		List<Integer> guaranteeNoList = updateContract.getGuaranteeNoList();
		if(isOtherGuarantee(guaranteeNoList, contractNo)){
			throw new BusinessErrorCodeException(ErrorCode.ERROR14);
		}

		List<GuaranteeResponse> curGuaranteeList = insuranceSelectService.selectGuaranteeList(contractNo);
		boolean isContainGuarantee = curGuaranteeList.stream()
				.map(GuaranteeResponse::getGuaranteeNo)
				.anyMatch(guaranteeNoList::contains);

		if(isContainGuarantee){
			throw new BusinessErrorCodeException(ErrorCode.ERROR15);
		}
	}

	private boolean isOtherGuarantee(List<Integer> guaranteeNoList, int contractNo){
		List<GuaranteeResponse> requestGuaranteeList = insuranceSelectService.selectGuaranteeList(guaranteeNoList);
		int productNo = insuranceSelectService.getContractInfo(contractNo).getProductNo();

		return requestGuaranteeList.stream()
				.map(GuaranteeResponse::getProductNo)
				.anyMatch(v -> v != productNo);
	}
}
