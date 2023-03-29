package com.insurance.kakao.insurace.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.insurance.kakao.insurace.mapper.InsuranceCommandMapper;
import com.insurance.kakao.insurace.model.response.GuaranteeResponse;
import com.insurance.kakao.insurace.model.vo.UpdateContract;
import com.insurance.kakao.insurace.service.update.InsuranceModifiable;

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
		insuranceModifiable.isNotUpdate(updateContract);
		insuranceModifiable.insuranceUpdate(updateContract);

		int contractNo = updateContract.getContractNo();
		double totalAmount = getTotalAmount(contractNo);
		command.updateTotalAmount(contractNo, totalAmount);
	}

	private double getTotalAmount(int contractNo){
		List<GuaranteeResponse> guaranteeList = insuranceSelectService.selectGuaranteeList(contractNo);
		int contractPeriod = insuranceSelectService.getContractInfo(contractNo).getContractPeriod();
		return insuranceSelectService.getTotalAmount(guaranteeList, contractPeriod);
	}
}
