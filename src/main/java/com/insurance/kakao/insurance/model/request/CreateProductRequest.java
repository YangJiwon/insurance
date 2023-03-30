package com.insurance.kakao.insurance.model.request;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "상품 등록 요청")
public class CreateProductRequest {
	@NotBlank
	@Schema(description = "상품 명", example = "여행자 보험")
	private String productName;

	@Min(1)
	@Schema(description = "최소 계약 기간", example = "1")
	private int minPeriod;

	@Min(1)
	@Schema(description = "최대 계약 기간", example = "3")
	private int maxPeriod;

	@Valid
	@NotEmpty
	@Schema(description = "담보 리스트")
	private List<CreateGuaranteeRequest> guaranteeRequestList;

	@Schema(description = "상품 번호", hidden = true)
	private int productNo;
}
