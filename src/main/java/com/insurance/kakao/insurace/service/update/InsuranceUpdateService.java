package com.insurance.kakao.insurace.service.update;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.insurance.kakao.insurace.common.exception.BusinessErrorCodeException;
import com.insurance.kakao.insurace.common.exception.ErrorCode;
import com.insurance.kakao.insurace.mapper.InsuranceCommandMapper;
import com.insurance.kakao.insurace.model.response.GuaranteeResponse;
import com.insurance.kakao.insurace.model.vo.UpdateContract;
import com.insurance.kakao.insurace.service.InsuranceSelectService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InsuranceUpdateService {
	private final Map<String, InsuranceModifiable> insuranceModifiableMap;
	private final InsuranceSelectService insuranceSelectService;
	private final InsuranceCommandMapper command;

	@Transactional
	public void updateContract(UpdateContract updateContract){
		InsuranceModifiable insuranceModifiable = insuranceModifiableMap.get(updateContract.getServiceName());
		insuranceModifiable.validation(updateContract);
		insuranceModifiable.insuranceUpdate(updateContract);

		int contractNo = updateContract.getContractNo();
		double totalAmount = getTotalAmount(contractNo);
		if(command.updateTotalAmount(contractNo, totalAmount) != 1){
			throw new BusinessErrorCodeException(ErrorCode.ERROR20);
		}
	}

	private double getTotalAmount(int contractNo){
		List<GuaranteeResponse> guaranteeList = insuranceSelectService.selectGuaranteeList(contractNo);
		int contractPeriod = insuranceSelectService.getContractInfo(contractNo).getContractPeriod();
		return insuranceSelectService.getTotalAmount(guaranteeList, contractPeriod);
	}
}
