package com.insurance.kakao.insurace.service.update;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.insurance.kakao.insurace.common.CommonUtil;
import com.insurance.kakao.insurace.common.exception.BusinessErrorCodeException;
import com.insurance.kakao.insurace.common.exception.ErrorCode;
import com.insurance.kakao.insurace.mapper.InsuranceCommandMapper;
import com.insurance.kakao.insurace.model.response.ProductResponse;
import com.insurance.kakao.insurace.model.vo.UpdateContract;
import com.insurance.kakao.insurace.service.InsuranceSelectService;

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
			throw new BusinessErrorCodeException(ErrorCode.ERROR12);
		}
	}

	@Override
	public void validation(UpdateContract updateContract){
		int contractNo = updateContract.getContractNo();
		int contractPeriod = updateContract.getContractPeriod();
		int productNo = insuranceSelectService.getContractInfo(contractNo).getProductNo();

		ProductResponse product = insuranceSelectService.getProductInfo(productNo, contractPeriod);
		if(product.isNotValidPeriod(contractPeriod)){
			throw new BusinessErrorCodeException(ErrorCode.ERROR3);
		}

		LocalDate endDate = getEndDate(contractNo, contractPeriod);
		if(LocalDate.now().isAfter(endDate)){
			throw new BusinessErrorCodeException(ErrorCode.ERROR11);
		}
	}

	private LocalDate getEndDate(int contractNo, int contractPeriod){
		LocalDate startDate = insuranceSelectService.getContractInfo(contractNo).getInsuranceStartDate();
		return CommonUtil.plusMonth(startDate, contractPeriod);
	}
}
