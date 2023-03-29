package com.insurance.kakao.insurace.service.update;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.insurance.kakao.insurace.common.CommonUtil;
import com.insurance.kakao.insurace.common.exception.BusinessErrorCodeException;
import com.insurance.kakao.insurace.common.exception.ErrorCode;
import com.insurance.kakao.insurace.mapper.InsuranceCommandMapper;
import com.insurance.kakao.insurace.model.vo.UpdateContract;
import com.insurance.kakao.insurace.service.InsuranceSelectService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
class UpdateContractPeriodService implements InsuranceModifiable {
	private final InsuranceSelectService insuranceSelectService;
	private final InsuranceCommandMapper command;

	@Override
	public void insuranceUpdate(UpdateContract updateContract) {
		int contractNo = updateContract.getContractNo();
		int contractPeriod = updateContract.getContractPeriod();

		LocalDate startDate = insuranceSelectService.getContractInfo(contractNo).getInsuranceStartDate();
		LocalDate endDate = CommonUtil.plusMonth(startDate, contractPeriod);

		if(LocalDate.now().isAfter(endDate)){
			throw new BusinessErrorCodeException(ErrorCode.ERROR11);
		}

		if(command.updateContractPeriod(contractNo, endDate, contractPeriod) != 1){
			throw new BusinessErrorCodeException(ErrorCode.ERROR11);
		}
	}
}
