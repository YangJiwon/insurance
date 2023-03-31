package com.insurance.kakao.insurance.model.request;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "담보 등록 요청")
public class CreateGuaranteeRequest {
	@NotBlank
	@Schema(description = "담보 명", example = "부분손실")
	private String guaranteeName;

	@Min(1)
	@Schema(description = "가입금액", example = "100000")
	private double subscriptionAmount;

	@Min(1)
	@Schema(description = "기준금액", example = "500")
	private double standardAmount;

	@Schema(description = "담보번호", hidden = true)
	private int guaranteeNo;

}
