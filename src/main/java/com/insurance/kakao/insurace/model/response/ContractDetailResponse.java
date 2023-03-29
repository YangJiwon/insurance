package com.insurance.kakao.insurace.model.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ContractDetailResponse {
	private String contractName;
	private int contractPeriod;
	private LocalDate insuranceStartDate;
	private LocalDate insuranceEndDate;
	private LocalDateTime registrationDate;

	private LocalDateTime updateDate;
	private double totalAmount;
	private String contractStatus;
	private String productName;
	private List<GuaranteeResponse> guaranteeNameList;
}
