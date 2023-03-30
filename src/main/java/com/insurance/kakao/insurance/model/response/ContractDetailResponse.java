package com.insurance.kakao.insurance.model.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "계약 조회 응답")
public class ContractDetailResponse {
	@Schema(description = "계약번호", example = "1")
	private int contractNo;

	@Schema(description = "계약명", example = "테스트 계약")
	private String contractName;

	@Schema(description = "계약 기간", example = "3")
	private int contractPeriod;

	@Schema(description = "보험 시작일", example = "2023-04-01")
	private LocalDate insuranceStartDate;

	@Schema(description = "보험 종료일", example = "2023-07-01")
	private LocalDate insuranceEndDate;

	@Schema(description = "등록일", example = "2023-04-01 14:23:33")
	private LocalDateTime registrationDate;

	@Schema(description = "수정일", example = "2023-04-02 16:76:21")
	private LocalDateTime updateDate;

	@Schema(description = "총 보험료", example = "104000")
	private double totalAmount;

	@Schema(description = "계약 상태", example = "N")
	private String contractStatus;

	@Schema(description = "상품번호", example = "1")
	private int productNo;

	@Schema(description = "상품명", example = "여행자 보험")
	private String productName;

	@Schema(description = "최소 계약 기간", example = "1")
	private int minPeriod;

	@Schema(description = "최대 계약 기간", example = "12")
	private int maxPeriod;

	@Schema(description = "담보 리스트")
	private List<GuaranteeResponse> guaranteeNameList;
}
