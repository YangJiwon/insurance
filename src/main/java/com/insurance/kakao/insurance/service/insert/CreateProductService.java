 package com.insurance.kakao.insurance.service.insert;

 import java.util.List;

 import org.springframework.stereotype.Service;
 import org.springframework.transaction.annotation.Transactional;

 import com.insurance.kakao.insurance.common.exception.BusinessErrorCodeException;
 import com.insurance.kakao.insurance.common.exception.ErrorCode;
 import com.insurance.kakao.insurance.mapper.InsuranceCommandMapper;
 import com.insurance.kakao.insurance.model.request.CreateGuaranteeRequest;
 import com.insurance.kakao.insurance.model.request.CreateProductRequest;
 import com.insurance.kakao.insurance.model.vo.CreateInsurance;

 import lombok.RequiredArgsConstructor;

 @Service
 @RequiredArgsConstructor
 class CreateProductService implements InsuranceCreatable {
	 private final InsuranceCommandMapper command;
	 private final CreateGuaranteeService createGuaranteeService;

	 @Override
	 @Transactional
	 public void create(CreateInsurance createInsurance) {
		 CreateProductRequest product = createInsurance.getCreateProductRequest();
		 if(command.insertProduct(product) != 1){
			 throw new BusinessErrorCodeException(ErrorCode.INSERT_PRODUCT);
		 }

		 int productNo = product.getProductNo();
		 List<CreateGuaranteeRequest> createGuaranteeRequestList = product.getGuaranteeRequestList();

		 createGuaranteeService.insertGuarantee(productNo, createGuaranteeRequestList);
	 }
 }
