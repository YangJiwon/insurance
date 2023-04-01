package com.insurance.kakao.insurance.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.insurance.kakao.insurance.model.request.CreateContractRequest;
import com.insurance.kakao.insurance.model.request.CreateGuaranteeRequest;
import com.insurance.kakao.insurance.model.request.CreateProductRequest;
import com.insurance.kakao.insurance.model.request.UpdateContractPeriodRequest;
import com.insurance.kakao.insurance.model.request.UpdateContractStatusRequest;
import com.insurance.kakao.insurance.model.request.UpdateGuaranteeRequest;
import com.insurance.kakao.insurance.model.response.ContractDetailResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Validated
@Tag(name = "계약관리 시스템", description = "계약관리 시스템 API")
public interface InsuranceControllerApi {
	@Operation(
			summary = "계약 생성",
			description = "최초 계약 생성시 상태는 정상계약으로 간주합니다. " +
						  "총 보험료는 계약 생성시점에 서버에서 계산합니다.",
			responses = {
					@ApiResponse(description = "Success",
							responseCode = "200",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Integer.class)))
			}
	)
	@PostMapping(value = "/contract")
	ResponseEntity<?> createContract(@Valid @RequestBody CreateContractRequest createContractRequest);

	@Operation(
			summary = "담보 추가",
			description = "해당 계약에 담보를 추가합니다",
			responses = {
					@ApiResponse(description = "Success",
							responseCode = "200",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema()))
			}
	)
	@PostMapping(value = "/contract/guarantee")
	ResponseEntity<?> insertGuaranteeOfContract(@Valid @RequestBody UpdateGuaranteeRequest updateGuaranteeRequest);

	@Operation(
			summary = "담보 삭제",
			description = "해당 계약에 담보를 삭제합니다",
			responses = {
					@ApiResponse(description = "Success",
							responseCode = "200",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema()))
			}
	)
	@DeleteMapping(value = "/contract/guarantee")
	ResponseEntity<?> deleteGuaranteeOfContract(@Valid @RequestBody UpdateGuaranteeRequest updateGuaranteeRequest);

	@Operation(
			summary = "계약 기간 변경",
			description = "해당 계약의 기간을 변경합니다(시작일은 변경 불가)",
			responses = {
					@ApiResponse(description = "Success",
							responseCode = "200",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema()))
			}
	)
	@PutMapping(value = "/contract/period")
	ResponseEntity<?> updateContractPeriod(@Valid @RequestBody UpdateContractPeriodRequest updateContractPeriodRequest);

	@Operation(
			summary = "계약 상태 변경",
			description = "해당 계약의 상태를 변경합니다(단, 기간만료 상태에서는 변경 불가)",
			responses = {
					@ApiResponse(description = "Success",
							responseCode = "200",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema()))
			}
	)
	@PutMapping(value = "/contract/status")
	ResponseEntity<?> updateContractStatus(@Valid @RequestBody UpdateContractStatusRequest updateContractStatusRequest);

	@Operation(
			summary = "예상 보험료 계산",
			description = "보험 가입 전 보험료를 미리 산출해 보기 위한 기능입니다. " +
						  "상품/담보 정보와 계약기간을 통해서 예상되는 보험료를 리턴합니다.",
			responses = {
					@ApiResponse(description = "Success",
							responseCode = "200",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Double.class)))
			}
	)
	@GetMapping(value = "/estimate-amount")
	ResponseEntity<?> getEstimateAmount(@Parameter(name = "guarantees", description = "담보 번호")
										@RequestParam(name = "guarantees") String guarantees,
										@Parameter(name = "contractPeriod", description = "계약 기간")
										@RequestParam(name = "contractPeriod") int contractPeriod);

	@Operation(
			summary = "계약 정보 조회",
			description = "계약 정보를 전달받아서 해당 계약의 상세 내용을 리턴합니다.",
			responses = {
					@ApiResponse(description = "Success",
							responseCode = "200",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ContractDetailResponse.class)))
			}
	)
	@GetMapping(value = "/{contractNo}/contract")
	ResponseEntity<?> getContractDetail(@Parameter(name = "contractNo", description = "계약 번호")
										@PathVariable(name = "contractNo") @Min(1) int contractNo);

	@Operation(
			summary = "새로운 상품 등록",
			description = "새로운 상품을 등록합니다.",
			responses = {
					@ApiResponse(description = "Success",
							responseCode = "200",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema()))
			}
	)
	@PostMapping(value = "/product")
	ResponseEntity<?> createProduct(@Valid @RequestBody CreateProductRequest createProductRequest);

	@Operation(
			summary = "새로운 담보 등록",
			description = "기존 상품에 새로운 담보를 등록합니다.",
			responses = {
					@ApiResponse(description = "Success",
							responseCode = "200",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema()))
			}
	)
	@PostMapping(value = "/{productNo}/guarantee")
	ResponseEntity<?> createGuarantee(@Parameter(name = "productNo", description = "상품 번호")
									  @PathVariable(name = "productNo") @Min(1) int productNo,
									  @Valid @RequestBody @NotEmpty List<CreateGuaranteeRequest> createGuaranteeRequestList);
}
