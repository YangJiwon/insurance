 package com.insurance.kakao.insurance.service.insert;

 import java.time.LocalDate;
 import java.util.List;

 import org.springframework.stereotype.Service;
 import org.springframework.transaction.annotation.Transactional;

 import com.insurance.kakao.insurance.common.exception.BusinessErrorCodeException;
 import com.insurance.kakao.insurance.common.exception.ErrorCode;
 import com.insurance.kakao.insurance.mapper.InsuranceCommandMapper;
 import com.insurance.kakao.insurance.model.enums.ContractStatusEnum;
 import com.insurance.kakao.insurance.model.request.CreateContractRequest;
 import com.insurance.kakao.insurance.model.response.GuaranteeResponse;
 import com.insurance.kakao.insurance.model.response.ProductResponse;
 import com.insurance.kakao.insurance.model.vo.CreateContract;
 import com.insurance.kakao.insurance.model.vo.CreateInsurance;
 import com.insurance.kakao.insurance.service.InsuranceSelectService;

 import lombok.RequiredArgsConstructor;

 @Service
 @RequiredArgsConstructor
 class CreateContractService implements InsuranceCreatable {
	 private final InsuranceCommandMapper command;
	 private final InsuranceSelectService insuranceSelectService;

	 @Override
	 @Transactional
	 public void create(CreateInsurance createInsurance) {
		 CreateContractRequest createContractRequest = createInsurance.getCreateContractRequest();
		 LocalDate startDate = createContractRequest.getInsuranceStartDate();

		 if (startDate.isBefore(LocalDate.now())) {
			 throw new BusinessErrorCodeException(ErrorCode.NOT_VALID_START_DATE);
		 }

		 int contractPeriod = createContractRequest.getContractPeriod();
		 int productNo = createContractRequest.getProductNo();

		 List<Integer> guaranteeNoList = createContractRequest.getGuaranteeNoList();
		 long notExistGuaranteeCount = insuranceSelectService.notExistGuaranteeCount(productNo, guaranteeNoList);
		 if(notExistGuaranteeCount > 0){
			 throw new BusinessErrorCodeException(ErrorCode.NOT_VALID_GUARANTEE);
		 }

		 List<GuaranteeResponse> guaranteeList = insuranceSelectService.selectGuaranteeList(guaranteeNoList);
		 ProductResponse product = insuranceSelectService.getProductInfo(productNo);
		 if(product.isNotValidPeriod(contractPeriod)) {
			 throw new BusinessErrorCodeException(ErrorCode.NOT_VALID_CONTRACT_PERIOD);
		 }

		 double totalAmount = insuranceSelectService.getTotalAmount(guaranteeList, contractPeriod);
		 CreateContract createContract = CreateContract.builder()
				 .contractName(createContractRequest.getContractName())
				 .contractPeriod(contractPeriod)
				 .productNo(productNo)
				 .totalAmount(totalAmount)
				 .confirmStatus(ContractStatusEnum.NORMAL.getStatus())
				 .insuranceStartDate(startDate)
				 .insuranceEndDate(createContractRequest.getInsuranceEndDate())
				 .guaranteeNoList(guaranteeNoList)
				 .build();

		 if (command.insertContract(createContract) != 1) {
			 throw new BusinessErrorCodeException(ErrorCode.INSERT_CONTRACT);
		 }

		 int contractNo = createContract.getContractNo();
		 if (command.insertGuaranteeOfContract(contractNo, guaranteeNoList) != guaranteeNoList.size()) {
			 throw new BusinessErrorCodeException(ErrorCode.INSERT_GUARANTEE_OF_CONTRACT);
		 }
	 }
 }
