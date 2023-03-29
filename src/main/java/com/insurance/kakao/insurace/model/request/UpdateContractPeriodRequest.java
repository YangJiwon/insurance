package com.insurance.kakao.insurace.model.request;

import javax.validation.constraints.Min;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class UpdateContractPeriodRequest {
	@Min(0)
	@Schema(description = "계약 번호")
	private int contractNo;

	@Min(0)
	@Schema(description = "계약 기간")
	private int contractPeriod;
}
