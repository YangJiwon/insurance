package com.insurance.kakao.insurance.model.response;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class ProductResponse {
	private int productNo;
	private String productName;
	private int minPeriod;
	private int maxPeriod;

	public boolean isNotValidPeriod(int contractPeriod){
		return contractPeriod > this.maxPeriod || contractPeriod < this.minPeriod;
	}
}
