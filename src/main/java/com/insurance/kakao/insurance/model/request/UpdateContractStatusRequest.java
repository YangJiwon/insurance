package com.insurance.kakao.insurance.model.request;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.insurance.kakao.insurance.model.enums.ContractStatusEnum;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class UpdateContractStatusRequest {
	@Min(1)
	@Schema(description = "계약 번호", example = "1")
	private int contractNo;

	@NotNull
	@Schema(description = "계약 상태(NORMAL-정상계약, WITHDRAW-청약철회, EXPIRE-기간만료)", example = "NORMAL")
	private ContractStatusEnum contractStatus;
}
