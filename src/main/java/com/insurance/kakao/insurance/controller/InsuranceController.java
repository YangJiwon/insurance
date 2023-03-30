package com.insurance.kakao.insurance.controller;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.insurance.kakao.insurance.model.request.CreateContractRequest;
import com.insurance.kakao.insurance.model.request.CreateGuaranteeRequest;
import com.insurance.kakao.insurance.model.request.CreateProductRequest;
import com.insurance.kakao.insurance.model.request.UpdateContractPeriodRequest;
import com.insurance.kakao.insurance.model.request.UpdateContractStatusRequest;
import com.insurance.kakao.insurance.model.request.UpdateGuaranteeRequest;
import com.insurance.kakao.insurance.model.response.GuaranteeResponse;
import com.insurance.kakao.insurance.model.vo.UpdateContract;
import com.insurance.kakao.insurance.service.InsuranceSelectService;
import com.insurance.kakao.insurance.service.insert.InsuranceInsertService;
import com.insurance.kakao.insurance.service.update.InsuranceUpdateService;
import com.insurance.kakao.insurance.service.update.UpdateContractServiceEnums;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class InsuranceController implements InsuranceControllerApi{
	private final InsuranceSelectService insuranceSelectService;
	private final InsuranceInsertService insuranceInsertService;
	private final InsuranceUpdateService insuranceUpdateService;

	@Override
	public ResponseEntity<?> createContract(CreateContractRequest contract){
		insuranceInsertService.createContract(contract);
		return ResponseEntity.ok().build();
	}

	@Override
	public ResponseEntity<?> insertGuaranteeOfContract(UpdateGuaranteeRequest guarantee) {
		UpdateContract updateContract = UpdateContract.builder()
				.contractNo(guarantee.getContractNo())
				.guaranteeNoList(guarantee.getGuaranteeNoList())
				.serviceName(UpdateContractServiceEnums.INSERT_GUARANTEE.getName())
				.build();

		insuranceUpdateService.updateContract(updateContract);
		return ResponseEntity.ok().build();
	}

	@Override
	public ResponseEntity<?> deleteGuaranteeOfContract(UpdateGuaranteeRequest guarantee) {
		UpdateContract updateContract = UpdateContract.builder()
				.contractNo(guarantee.getContractNo())
				.guaranteeNoList(guarantee.getGuaranteeNoList())
				.serviceName(UpdateContractServiceEnums.DELETE_GUARANTEE.getName())
				.build();

		insuranceUpdateService.updateContract(updateContract);
		return ResponseEntity.ok().build();
	}

	@Override
	public ResponseEntity<?> updateContractPeriod(UpdateContractPeriodRequest contractPeriod) {
		UpdateContract updateContract = UpdateContract.builder()
				.contractNo(contractPeriod.getContractNo())
				.contractPeriod(contractPeriod.getContractPeriod())
				.serviceName(UpdateContractServiceEnums.UPDATE_PERIOD.getName())
				.build();

		insuranceUpdateService.updateContract(updateContract);
		return ResponseEntity.ok().build();
	}

	@Override
	public ResponseEntity<?> updateContractStatus(UpdateContractStatusRequest contractStatus) {
		UpdateContract updateContract = UpdateContract.builder()
				.contractNo(contractStatus.getContractNo())
				.contractStatus(contractStatus.getContractStatus())
				.serviceName(UpdateContractServiceEnums.UPDATE_STATUS.getName())
				.build();

		insuranceUpdateService.updateContract(updateContract);
		return ResponseEntity.ok().build();
	}

	@Override
	public ResponseEntity<?> getEstimateAmount(String guarantees, int contractPeriod){
		String[] split = guarantees.split(",");//todo::
		List<Integer> guaranteeNoList = Arrays.stream(split).map(Integer::parseInt).collect(Collectors.toList());

		List<GuaranteeResponse> guaranteeList = insuranceSelectService.selectGuaranteeList(guaranteeNoList);
		return ResponseEntity.ok(insuranceSelectService.getTotalAmount(guaranteeList, contractPeriod));
	}

	@Override
	public ResponseEntity<?> getContractDetail(int contractNo){
		return ResponseEntity.ok(insuranceSelectService.getContractDetail(contractNo));
	}

	@Override
	public ResponseEntity<?> createProduct(CreateProductRequest createProduct) {
		insuranceInsertService.createProduct(createProduct);
		return ResponseEntity.ok().build();
	}

	@Override
	public ResponseEntity<?> createGuarantee(int productNo, List<CreateGuaranteeRequest> createGuaranteeList){
		insuranceInsertService.createGuarantee(productNo, createGuaranteeList);
		return ResponseEntity.ok().build();
	}
}
