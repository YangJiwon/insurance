package com.insurance.kakao.insurace.model.request;

import javax.validation.constraints.Min;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class UpdateContractPeriodRequest {
	@Min(1)
	@Schema(description = "계약 번호", example = "1")
	private int contractNo;

	@Min(1)
	@Schema(description = "계약 기간", example = "2")
	private int contractPeriod;
}
