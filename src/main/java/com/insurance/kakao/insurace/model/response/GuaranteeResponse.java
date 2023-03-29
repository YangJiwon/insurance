package com.insurance.kakao.insurace.model.response;

import lombok.Getter;

@Getter
public class GuaranteeResponse {
	private int guaranteeNo;
	private String guaranteeName;
	private int productNo;
	private double subscriptionAmount;
	private double standardAmount;
}
