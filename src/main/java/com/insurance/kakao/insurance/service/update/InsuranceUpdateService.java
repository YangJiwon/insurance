package com.insurance.kakao.insurance.service.update;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.insurance.kakao.insurance.common.exception.BusinessErrorCodeException;
import com.insurance.kakao.insurance.common.exception.ErrorCode;
import com.insurance.kakao.insurance.mapper.InsuranceCommandMapper;
import com.insurance.kakao.insurance.model.enums.ContractStatusEnum;
import com.insurance.kakao.insurance.model.vo.UpdateContract;
import com.insurance.kakao.insurance.service.InsuranceSelectService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InsuranceUpdateService {
	private final Map<String, InsuranceModifiable> insuranceModifiableMap;
	private final InsuranceSelectService insuranceSelectService;
	private final InsuranceCommandMapper command;

	@Transactional
	public void updateContract(UpdateContract updateContract){
		int contractNo = updateContract.getContractNo();
		String status = insuranceSelectService.getContractInfo(contractNo).getContractStatus();
		if(ContractStatusEnum.isExpire(status)){
			throw new BusinessErrorCodeException(ErrorCode.UPDATE_EXPIRE_CONTRACT);
		}

		InsuranceModifiable insuranceModifiable = insuranceModifiableMap.get(updateContract.getServiceName());
		insuranceModifiable.validation(updateContract);
		insuranceModifiable.insuranceUpdate(updateContract);

		if(insuranceModifiable.isUpdateOnlyDate()){
			if(command.updateOnlyDate(contractNo) != 1){
				throw new BusinessErrorCodeException(ErrorCode.UPDATE_ONLY_DATE);
			}
			return;
		}

		double totalAmount = insuranceSelectService.getTotalAmount(contractNo);
		if(command.updateTotalAmount(contractNo, totalAmount) != 1){
			throw new BusinessErrorCodeException(ErrorCode.UPDATE_TOTAL_AMOUNT);
		}
	}
}
