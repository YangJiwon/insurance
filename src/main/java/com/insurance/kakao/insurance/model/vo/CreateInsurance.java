package com.insurance.kakao.insurance.model.vo;

import java.util.List;

import com.insurance.kakao.insurance.model.request.CreateContractRequest;
import com.insurance.kakao.insurance.model.request.CreateGuaranteeRequest;
import com.insurance.kakao.insurance.model.request.CreateProductRequest;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateInsurance {
	private CreateContractRequest createContractRequest;
	private CreateProductRequest createProductRequest;
	private List<CreateGuaranteeRequest> createGuaranteeRequest;
	private int productNo;
	private String serviceName;
}
