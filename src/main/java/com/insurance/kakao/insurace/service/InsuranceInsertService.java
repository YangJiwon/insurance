package com.insurance.kakao.insurace.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.insurance.kakao.insurace.common.exception.BusinessErrorCodeException;
import com.insurance.kakao.insurace.common.exception.ErrorCode;
import com.insurance.kakao.insurace.mapper.InsuranceCommandMapper;
import com.insurance.kakao.insurace.model.enums.ContractStatusEnum;
import com.insurance.kakao.insurace.model.request.CreateContractRequest;
import com.insurance.kakao.insurace.model.response.GuaranteeResponse;
import com.insurance.kakao.insurace.model.response.ProductResponse;
import com.insurance.kakao.insurace.model.vo.CreateContract;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InsuranceInsertService {
	private final InsuranceCommandMapper command;
	private final InsuranceSelectService insuranceSelectService;

	@Transactional
	public void insertContract(CreateContractRequest contract){
		LocalDate endDate = contract.getInsuranceEndDate();
		if(LocalDate.now().isAfter(endDate)){
			throw new BusinessErrorCodeException(ErrorCode.ERROR11);
		}

		List<Integer> guaranteeNoList = contract.getGuaranteeNoList();
		List<GuaranteeResponse> guaranteeList = insuranceSelectService.selectGuaranteeList(guaranteeNoList);

		int productNo = guaranteeList.get(0).getProductNo();
		int contractPeriod = contract.getContractPeriod();
		double totalAmount = insuranceSelectService.getTotalAmount(guaranteeList, contractPeriod);
		ProductResponse product = insuranceSelectService.getProduct(productNo, contractPeriod);

		CreateContract createContract = CreateContract.builder()
				.contractName(contract.getContractName())
				.contractPeriod(contractPeriod)
				.productNo(productNo)
				.productName(product.getProductName())
				.totalAmount(totalAmount)
				.confirmStatus(ContractStatusEnum.NORMAL.getStatus())
				.insuranceStartDate(contract.getInsuranceStartDate())
				.insuranceEndDate(endDate)
				.guaranteeNoList(guaranteeNoList)
				.build();

		if(command.insertContract(createContract) != 1){
			throw new BusinessErrorCodeException(ErrorCode.ERROR1);
		}

		if(command.insertGuaranteeOfContract(createContract.getContractNo(), createContract.getGuaranteeNoList()) != 1) {
			throw new BusinessErrorCodeException(ErrorCode.ERROR7);
		}
	}
}
