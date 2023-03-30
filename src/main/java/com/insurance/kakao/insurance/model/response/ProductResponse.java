package com.insurance.kakao.insurance.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductResponse {
	private String productName;
	private int minPeriod;
	private int maxPeriod;

	public boolean isNotValidPeriod(int contractPeriod){
		return contractPeriod > this.maxPeriod || contractPeriod < this.minPeriod;
	}
}
