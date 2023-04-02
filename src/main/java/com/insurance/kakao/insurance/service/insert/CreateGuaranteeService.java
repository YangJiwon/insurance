 package com.insurance.kakao.insurance.service.insert;

 import java.util.List;
 import java.util.stream.Collectors;

 import org.springframework.stereotype.Service;

 import com.insurance.kakao.insurance.common.exception.BusinessErrorCodeException;
 import com.insurance.kakao.insurance.common.exception.ErrorCode;
 import com.insurance.kakao.insurance.mapper.InsuranceCommandMapper;
 import com.insurance.kakao.insurance.model.request.CreateGuaranteeRequest;
 import com.insurance.kakao.insurance.model.response.ProductResponse;
 import com.insurance.kakao.insurance.model.vo.CreateInsurance;
 import com.insurance.kakao.insurance.service.InsuranceSelectService;

 import lombok.RequiredArgsConstructor;

 @Service
 @RequiredArgsConstructor
 class CreateGuaranteeService implements InsuranceCreatable {
	 private final InsuranceCommandMapper command;
	 private final InsuranceSelectService insuranceSelectService;

	 @Override
	 public void create(CreateInsurance createInsurance) {
		 int productNo = createInsurance.getProductNo();
		 List<CreateGuaranteeRequest> guaranteeList = createInsurance.getCreateGuaranteeRequest();

		 ProductResponse product = insuranceSelectService.getProductInfo(productNo);
		 insertGuarantee(product.getProductNo(), guaranteeList);
	 }

	 void insertGuarantee(int productNo, List<CreateGuaranteeRequest> guaranteeList){
		 if(command.insertGuarantee(productNo, guaranteeList) != guaranteeList.size()){
			 throw new BusinessErrorCodeException(ErrorCode.INSERT_GUARANTEE);
		 }

		 List<Integer> guaranteeNoList = guaranteeList.stream()
				 .map(CreateGuaranteeRequest::getGuaranteeNo)
				 .collect(Collectors.toList());

		 if(command.insertGuaranteeOfProduct(productNo, guaranteeNoList) != guaranteeNoList.size()){
			 throw new BusinessErrorCodeException(ErrorCode.CREATE_GUARANTEE_MAPPING);
		 }
	 }
 }
