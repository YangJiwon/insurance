package com.insurance.kakao.insurace.model.response;

import lombok.Getter;

@Getter
public class ProductResponse {
	private String productName;
	private int minPeriod;
	private int maxPeriod;

	public boolean isNotValidPeriod(int contractPeriod){
		return contractPeriod > this.maxPeriod || contractPeriod < this.minPeriod;
	}
}
