package com.insurance.kakao.insurance.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GuaranteeResponse {
	@Schema(description = "담보 번호", example = "1")
	private int guaranteeNo;

	@Schema(description = "담보명", example = "상해치료비")
	private String guaranteeName;

	@Schema(description = "상품번호", example = "2")
	private int productNo;

	@Schema(description = "가입금액", example = "1000000")
	private double subscriptionAmount;

	@Schema(description = "기준금액", example = "100")
	private double standardAmount;

	public double getPaymentAmount(){
		return this.subscriptionAmount / this.standardAmount;
	}
}
