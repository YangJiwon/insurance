package com.insurance.kakao.insurance.model.vo;

import java.time.LocalDate;
import java.util.List;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
@EqualsAndHashCode
public class CreateContract {
	private int contractNo;
	private String contractName;
	private int contractPeriod;
	private int productNo;
	private String productName;

	private double totalAmount;
	private String confirmStatus;
	private LocalDate insuranceStartDate;
	private LocalDate insuranceEndDate;
	private List<Integer> guaranteeNoList;
}
