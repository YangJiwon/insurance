package com.insurance.kakao.insurace.model.request;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.insurance.kakao.insurace.model.enums.ContractStatusEnum;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class UpdateContractStatusRequest {
	@Min(1)
	@Schema(description = "계약 번호", example = "1")
	private int contractNo;

	@NotNull
	@Schema(description = "계약 상태", example = "NORMAL")
	private ContractStatusEnum contractStatus;
}
