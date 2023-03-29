package com.insurance.kakao.insurace.model.request;

import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class UpdateGuaranteeRequest {
	@Min(0)
	@Schema(description = "계약 번호")
	private int contractNo;

	@NotEmpty
	@Schema(description = "추가/삭제 담보 번호 리스트")
	private List<Integer> guaranteeNoList;
}
