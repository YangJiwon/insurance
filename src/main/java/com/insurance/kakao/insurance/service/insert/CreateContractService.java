 package com.insurance.kakao.insurance.service.insert;

 import java.time.LocalDate;
 import java.util.List;

 import org.springframework.stereotype.Service;

 import com.insurance.kakao.insurance.common.exception.BusinessErrorCodeException;
 import com.insurance.kakao.insurance.common.exception.ErrorCode;
 import com.insurance.kakao.insurance.mapper.InsuranceCommandMapper;
 import com.insurance.kakao.insurance.model.enums.ContractStatusEnum;
 import com.insurance.kakao.insurance.model.request.CreateContractRequest;
 import com.insurance.kakao.insurance.model.response.GuaranteeResponse;
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
	 public void create(CreateInsurance createInsurance) {
		 CreateContractRequest contract = createInsurance.getCreateContractRequest();
		 CreateContract createContract = getCreateContract(contract);

		 if (command.insertContract(createContract) != 1) {
			 throw new BusinessErrorCodeException(ErrorCode.INSERT_CONTRACT);
		 }

		 List<Integer> guaranteeNoList = contract.getGuaranteeNoList();
		 if (command.insertGuaranteeOfContract(createContract.getContractNo(), guaranteeNoList) != guaranteeNoList.size()) {
			 throw new BusinessErrorCodeException(ErrorCode.INSERT_GUARANTEE_OF_CONTRACT);
		 }
	 }

	 @Override
	 public void validation(CreateInsurance createInsurance){
		 CreateContractRequest contract = createInsurance.getCreateContractRequest();
		 LocalDate startDate = contract.getInsuranceStartDate();
		 if (startDate.isBefore(LocalDate.now())) {
			 throw new BusinessErrorCodeException(ErrorCode.NOT_VALID_START_DATE);
		 }

		 int productNo = contract.getProductNo();
		 List<Integer> guaranteeNoList = contract.getGuaranteeNoList();
		 long notExistGuaranteeCount = insuranceSelectService.notExistGuaranteeCount(productNo, guaranteeNoList);
		 if(notExistGuaranteeCount > 0){
			 throw new BusinessErrorCodeException(ErrorCode.NOT_VALID_GUARANTEE);
		 }

		 insuranceSelectService.validProductInfo(productNo, contract.getContractPeriod());
	 }

	 private CreateContract getCreateContract(CreateContractRequest contract){
		 List<Integer> guaranteeNoList = contract.getGuaranteeNoList();
		 int contractPeriod = contract.getContractPeriod();

		 return CreateContract.builder()
				 .contractName(contract.getContractName())
				 .contractPeriod(contractPeriod)
				 .productNo(contract.getProductNo())
				 .totalAmount(getTotalAmount(guaranteeNoList, contractPeriod))
				 .confirmStatus(ContractStatusEnum.NORMAL.getStatus())
				 .insuranceStartDate(contract.getInsuranceStartDate())
				 .insuranceEndDate(contract.getInsuranceEndDate())
				 .guaranteeNoList(guaranteeNoList)
				 .build();
	 }

	 private double getTotalAmount(List<Integer> guaranteeNoList, int contractPeriod){
		 List<GuaranteeResponse> guaranteeList = insuranceSelectService.selectGuaranteeList(guaranteeNoList);
		 return insuranceSelectService.getTotalAmount(guaranteeList, contractPeriod);
	 }
 }
