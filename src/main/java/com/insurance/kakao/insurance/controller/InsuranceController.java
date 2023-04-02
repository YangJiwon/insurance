package com.insurance.kakao.insurance.controller;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RestController;

import com.insurance.kakao.insurance.model.request.CreateContractRequest;
import com.insurance.kakao.insurance.model.request.CreateGuaranteeRequest;
import com.insurance.kakao.insurance.model.request.CreateProductRequest;
import com.insurance.kakao.insurance.model.request.UpdateContractPeriodRequest;
import com.insurance.kakao.insurance.model.request.UpdateContractStatusRequest;
import com.insurance.kakao.insurance.model.request.UpdateGuaranteeRequest;
import com.insurance.kakao.insurance.model.vo.CreateInsurance;
import com.insurance.kakao.insurance.model.vo.UpdateContract;
import com.insurance.kakao.insurance.service.InsuranceSelectService;
import com.insurance.kakao.insurance.service.insert.CreateInsuranceServiceEnum;
import com.insurance.kakao.insurance.service.insert.InsuranceCreateService;
import com.insurance.kakao.insurance.service.update.InsuranceUpdateService;
import com.insurance.kakao.insurance.service.update.UpdateContractServiceEnum;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class InsuranceController implements InsuranceControllerApi{
	private final InsuranceSelectService insuranceSelectService;
	private final InsuranceCreateService insuranceCreateService;
	private final InsuranceUpdateService insuranceUpdateService;

	@Override
	public ResponseEntity<?> createContract(CreateContractRequest contract){
		CreateInsurance createInsurance = CreateInsurance.builder()
				.createContractRequest(contract)
				.serviceName(CreateInsuranceServiceEnum.CONTRACT.getName())
				.build();

		insuranceCreateService.create(createInsurance);
		return ResponseEntity.ok().build();
	}

	@Override
	public ResponseEntity<?> insertGuaranteeOfContract(UpdateGuaranteeRequest guarantee) {
		UpdateContract updateContract = UpdateContract.builder()
				.contractNo(guarantee.getContractNo())
				.guaranteeNoList(guarantee.getGuaranteeNoList())
				.serviceName(UpdateContractServiceEnum.INSERT_GUARANTEE.getName())
				.build();

		insuranceUpdateService.updateContract(updateContract);
		return ResponseEntity.ok().build();
	}

	@Override
	public ResponseEntity<?> deleteGuaranteeOfContract(UpdateGuaranteeRequest guarantee) {
		UpdateContract updateContract = UpdateContract.builder()
				.contractNo(guarantee.getContractNo())
				.guaranteeNoList(guarantee.getGuaranteeNoList())
				.serviceName(UpdateContractServiceEnum.DELETE_GUARANTEE.getName())
				.build();

		insuranceUpdateService.updateContract(updateContract);
		return ResponseEntity.ok().build();
	}

	@Override
	public ResponseEntity<?> updateContractPeriod(UpdateContractPeriodRequest contractPeriod) {
		UpdateContract updateContract = UpdateContract.builder()
				.contractNo(contractPeriod.getContractNo())
				.contractPeriod(contractPeriod.getContractPeriod())
				.serviceName(UpdateContractServiceEnum.UPDATE_PERIOD.getName())
				.build();

		insuranceUpdateService.updateContract(updateContract);
		return ResponseEntity.ok().build();
	}

	@Override
	public ResponseEntity<?> updateContractStatus(UpdateContractStatusRequest contractStatus) {
		UpdateContract updateContract = UpdateContract.builder()
				.contractNo(contractStatus.getContractNo())
				.contractStatus(contractStatus.getContractStatus())
				.serviceName(UpdateContractServiceEnum.UPDATE_STATUS.getName())
				.build();

		insuranceUpdateService.updateContract(updateContract);
		return ResponseEntity.ok().build();
	}

	@Override
	public ResponseEntity<?> getEstimateAmount(String guarantees, int contractPeriod, int productNo){
		String[] split = guarantees.split(",");
		List<Integer> guaranteeNoList = Arrays.stream(split)
				.filter(v -> !ObjectUtils.isEmpty(v))
				.map(Integer::parseInt)
				.collect(Collectors.toList());

		return ResponseEntity.ok(insuranceSelectService.getEstimateAmount(guaranteeNoList, contractPeriod, productNo));
	}

	@Override
	public ResponseEntity<?> getContractDetail(int contractNo){
		return ResponseEntity.ok(insuranceSelectService.getContractDetail(contractNo));
	}

	@Override
	public ResponseEntity<?> createProduct(CreateProductRequest product) {
		CreateInsurance createInsurance = CreateInsurance.builder()
				.createProductRequest(product)
				.serviceName(CreateInsuranceServiceEnum.PRODUCT.getName())
				.build();

		insuranceCreateService.create(createInsurance);
		return ResponseEntity.ok().build();
	}

	@Override
	public ResponseEntity<?> createGuarantee(int productNo, List<CreateGuaranteeRequest> guaranteeList){
		CreateInsurance createInsurance = CreateInsurance.builder()
				.createGuaranteeRequest(guaranteeList)
				.productNo(productNo)
				.serviceName(CreateInsuranceServiceEnum.GUARANTEE.getName())
				.build();

		insuranceCreateService.create(createInsurance);
		return ResponseEntity.ok().build();
	}
}
