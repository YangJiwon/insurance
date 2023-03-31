package com.insurance.kakao.insurance.model.request;

import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class UpdateGuaranteeRequest {
	@Min(1)
	@Schema(description = "계약 번호", example = "1")
	private int contractNo;

	@NotEmpty
	@Schema(description = "추가/삭제 담보 번호 리스트")
	private List<Integer> guaranteeNoList;
}
