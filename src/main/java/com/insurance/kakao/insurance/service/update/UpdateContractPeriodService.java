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
	public void update(UpdateContract updateContract) {
		int contractNo = updateContract.getContractNo();
		int contractPeriod = updateContract.getContractPeriod();
		LocalDate endDate = getEndDate(contractNo, contractPeriod);

		if(command.updateContractPeriod(contractNo, endDate, contractPeriod) != 1){
			throw new BusinessErrorCodeException(ErrorCode.UPDATE_CONTRACT_PERIOD);
		}
	}

	@Override
	public void validation(UpdateContract updateContract){
		int contractNo = updateContract.getContractNo();
		int contractPeriod = updateContract.getContractPeriod();

		ContractResponse contract = insuranceSelectService.getContractInfo(contractNo);
		int curContractPeriod = contract.getContractPeriod();
		if(curContractPeriod == contractPeriod){
			throw new BusinessErrorCodeException(ErrorCode.SAME_CONTRACT_PERIOD);
		}

		int productNo = contract.getProductNo();
		ProductResponse product = insuranceSelectService.getProductInfo(productNo);
		if(product.isNotValidPeriod(contractPeriod)){
			throw new BusinessErrorCodeException(ErrorCode.NOT_VALID_CONTRACT_PERIOD);
		}

		LocalDate endDate = getEndDate(contract, contractPeriod);
		if(LocalDate.now().isAfter(endDate)){
			throw new BusinessErrorCodeException(ErrorCode.NOT_VALID_END_DATE);
		}
	}

	private LocalDate getEndDate(ContractResponse contract, int contractPeriod){
		LocalDate startDate = contract.getInsuranceStartDate();
		return CommonUtil.plusMonth(startDate, contractPeriod);
	}

	private LocalDate getEndDate(int contractNo, int contractPeriod){
		LocalDate startDate = insuranceSelectService.getContractInfo(contractNo).getInsuranceStartDate();
		return CommonUtil.plusMonth(startDate, contractPeriod);
	}
}
