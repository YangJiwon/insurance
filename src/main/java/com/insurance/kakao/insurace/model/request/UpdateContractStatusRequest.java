package com.insurance.kakao.insurace.model.request;

import javax.validation.constraints.Min;

import com.insurance.kakao.insurace.model.enums.ContractStatusEnum;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class UpdateContractStatusRequest {
	@Min(0)
	@Schema(description = "계약 번호")
	private int contractNo;

	@Schema(description = "계약 상태")
	private ContractStatusEnum contractStatus;
}
