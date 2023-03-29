package com.insurance.kakao.insurace.model.request;

import java.util.List;

import lombok.Getter;

@Getter
public class CalculateEstimateAmountRequest {
	private List<Integer> guaranteeNoList;
	private int contractPeriod;
}
