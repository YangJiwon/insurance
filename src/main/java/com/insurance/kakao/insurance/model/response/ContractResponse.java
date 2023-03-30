package com.insurance.kakao.insurance.model.response;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ContractResponse {
	private LocalDate insuranceStartDate;
	private int contractPeriod;
	private int productNo;
	private String contractStatus;
}
