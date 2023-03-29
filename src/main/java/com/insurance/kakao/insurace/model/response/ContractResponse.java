package com.insurance.kakao.insurace.model.response;

import java.time.LocalDate;

import lombok.Getter;

@Getter
public class ContractResponse {
	private LocalDate insuranceStartDate;
	private int contractPeriod;
	private int productNo;
	private String contractStatus;
}
