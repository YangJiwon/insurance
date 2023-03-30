 package com.insurance.kakao.insurance.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import com.insurance.kakao.insurance.common.exception.BusinessErrorCodeException;
import com.insurance.kakao.insurance.common.exception.ErrorCode;
import com.insurance.kakao.insurance.mapper.InsuranceCommandMapper;
import com.insurance.kakao.insurance.model.enums.ContractStatusEnum;
import com.insurance.kakao.insurance.model.request.CreateContractRequest;
import com.insurance.kakao.insurance.model.request.CreateGuaranteeRequest;
import com.insurance.kakao.insurance.model.request.CreateProductRequest;
import com.insurance.kakao.insurance.model.response.GuaranteeResponse;
import com.insurance.kakao.insurance.model.response.ProductResponse;
import com.insurance.kakao.insurance.model.vo.CreateContract;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InsuranceInsertService {
	private final InsuranceCommandMapper command;
	private final InsuranceSelectService insuranceSelectService;

	@Transactional
	public Integer createContract(CreateContractRequest contract) {
		LocalDate endDate = contract.getInsuranceEndDate();
		if (LocalDate.now().isAfter(endDate)) {
			throw new BusinessErrorCodeException(ErrorCode.ERROR11);
		}

		List<Integer> guaranteeNoList = contract.getGuaranteeNoList();
		List<GuaranteeResponse> guaranteeList = insuranceSelectService.selectGuaranteeList(guaranteeNoList);

		int productNo = guaranteeList.get(0).getProductNo();
		int contractPeriod = contract.getContractPeriod();
		double totalAmount = insuranceSelectService.getTotalAmount(guaranteeList, contractPeriod);

		ProductResponse product = insuranceSelectService.getProductInfo(productNo);
		if(product.isNotValidPeriod(contractPeriod)){
			throw new BusinessErrorCodeException(ErrorCode.ERROR3);
		}

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

		if (command.insertContract(createContract) != 1) {
			throw new BusinessErrorCodeException(ErrorCode.ERROR1);
		}

		int contractNo = createContract.getContractNo();
		if (command.insertGuaranteeOfContract(contractNo, guaranteeNoList) != guaranteeNoList.size()) {
			throw new BusinessErrorCodeException(ErrorCode.ERROR7);
		}

		return contractNo;
	}

	@Transactional
	public void createProduct(CreateProductRequest createProduct) {
		if(command.insertProduct(createProduct) != 1){
			throw new BusinessErrorCodeException(ErrorCode.ERROR21);
		}

		List<CreateGuaranteeRequest> createGuaranteeList = createProduct.getGuaranteeRequestList();
		if(command.insertGuarantee(createProduct.getProductNo(), createGuaranteeList) != createGuaranteeList.size()){
			throw new BusinessErrorCodeException(ErrorCode.ERROR22);
		}
	}

	@Transactional
	public void createGuarantee(int productNo, List<CreateGuaranteeRequest> createGuaranteeList) {
		ProductResponse product = insuranceSelectService.getProductInfo(productNo);
		if(ObjectUtils.isEmpty(product)){
			throw new BusinessErrorCodeException(ErrorCode.ERROR2);
		}

		if(command.insertGuarantee(productNo, createGuaranteeList) != createGuaranteeList.size()){
			throw new BusinessErrorCodeException(ErrorCode.ERROR22);
		}
	}
}
