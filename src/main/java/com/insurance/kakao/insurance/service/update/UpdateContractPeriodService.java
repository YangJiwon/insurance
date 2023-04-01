package com.insurance.kakao.insurance.service.update;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.insurance.kakao.insurance.common.CommonUtil;
import com.insurance.kakao.insurance.common.exception.BusinessErrorCodeException;
import com.insurance.kakao.insurance.common.exception.ErrorCode;
import com.insurance.kakao.insurance.mapper.InsuranceCommandMapper;
import com.insurance.kakao.insurance.model.response.ContractResponse;
import com.insurance.kakao.insurance.model.response.ProductResponse;
import com.insurance.kakao.insurance.model.vo.UpdateContract;
import com.insurance.kakao.insurance.service.InsuranceSelectService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
class UpdateContractPeriodService implements InsuranceModifiable {
	private final InsuranceSelectService insuranceSelectService;
	private final InsuranceCommandMapper command;

	@Override
	public void update(UpdateContract contract) {
		int contractNo = contract.getContractNo();
		int contractPeriod = contract.getContractPeriod();
		LocalDate endDate = getEndDate(contractNo, contractPeriod);

		if(command.updateContractPeriod(contractNo, endDate, contractPeriod) != 1){
			throw new BusinessErrorCodeException(ErrorCode.UPDATE_CONTRACT_PERIOD);
		}
	}

	@Override
	public void validation(UpdateContract contract){
		int contractNo = contract.getContractNo();
		int contractPeriod = contract.getContractPeriod();

		ContractResponse contractResponse = insuranceSelectService.getContractInfo(contractNo);
		int curContractPeriod = contractResponse.getContractPeriod();
		if(curContractPeriod == contractPeriod){
			throw new BusinessErrorCodeException(ErrorCode.SAME_CONTRACT_PERIOD);
		}

		int productNo = contractResponse.getProductNo();
		ProductResponse product = insuranceSelectService.getProductInfo(productNo);
		if(product.isNotValidPeriod(contractPeriod)){
			throw new BusinessErrorCodeException(ErrorCode.NOT_VALID_CONTRACT_PERIOD);
		}

		LocalDate endDate = getEndDate(contractResponse, contractPeriod);
		if(LocalDate.now().isAfter(endDate)){
			throw new BusinessErrorCodeException(ErrorCode.NOT_VALID_END_DATE);
		}
	}

	private LocalDate getEndDate(ContractResponse contractResponse, int contractPeriod){
		LocalDate startDate = contractResponse.getInsuranceStartDate();
		return CommonUtil.plusMonth(startDate, contractPeriod);
	}

	private LocalDate getEndDate(int contractNo, int contractPeriod){
		LocalDate startDate = insuranceSelectService.getContractInfo(contractNo).getInsuranceStartDate();
		return CommonUtil.plusMonth(startDate, contractPeriod);
	}
}
