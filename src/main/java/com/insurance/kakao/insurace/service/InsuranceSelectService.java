package com.insurance.kakao.insurace.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import com.insurance.kakao.insurace.common.exception.BusinessErrorCodeException;
import com.insurance.kakao.insurace.common.exception.ErrorCode;
import com.insurance.kakao.insurace.mapper.InsuranceQueryMapper;
import com.insurance.kakao.insurace.model.response.ContractDetailResponse;
import com.insurance.kakao.insurace.model.response.ContractResponse;
import com.insurance.kakao.insurace.model.response.GuaranteeResponse;
import com.insurance.kakao.insurace.model.response.ProductResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InsuranceSelectService {
	private final InsuranceQueryMapper query;

	public ProductResponse getProduct(int productNo, int contractPeriod){
		ProductResponse product = query.getProductInfo(productNo);
		if(ObjectUtils.isEmpty(product)){
			throw new BusinessErrorCodeException(ErrorCode.ERROR2);
		}

		if(contractPeriod > product.getMaxPeriod() || contractPeriod < product.getMinPeriod()){
			throw new BusinessErrorCodeException(ErrorCode.ERROR3);
		}
		return product;
	}

	public List<Integer> selectGuaranteeNoList(int contractNo){
		List<Integer> guaranteeNoList = query.selectGuaranteeNoList(contractNo);
		if(CollectionUtils.isEmpty(guaranteeNoList)){
			throw new BusinessErrorCodeException(ErrorCode.ERROR8);
		}
		return guaranteeNoList;
	}

	public List<GuaranteeResponse> selectGuaranteeList(int contractNo){
		List<Integer> guaranteeNoList = this.selectGuaranteeNoList(contractNo);
		return this.selectGuaranteeList(guaranteeNoList);
	}

	public List<GuaranteeResponse> selectGuaranteeList(List<Integer> guaranteeNoList){
		List<GuaranteeResponse> guaranteeList = query.selectGuaranteeList(guaranteeNoList);
		if(CollectionUtils.isEmpty(guaranteeList)){
			throw new BusinessErrorCodeException(ErrorCode.ERROR4);
		}

		boolean isSameProduct = guaranteeList.stream()
										.map(GuaranteeResponse::getProductNo)
										.distinct()
										.count() == 1;
		if(!isSameProduct){
			throw new BusinessErrorCodeException(ErrorCode.ERROR5);
		}
		return guaranteeList;
	}

	public double getTotalAmount(List<GuaranteeResponse> guaranteeList, int contractPeriod){
		double totalSum = guaranteeList.stream()
				.map(v -> v.getSubscriptionAmount() / v.getStandardAmount())
				.reduce(0d, Double::sum);
		return  Math.floor(contractPeriod * totalSum * 100.0) / 100.0;
	}

	public ContractResponse getContractInfo(int contractNo){
		return query.getContractInfo(contractNo);
	}

	public ContractDetailResponse getContractDetail(int contractNo){
		ContractDetailResponse contractDetailResponse = query.getContractDetail(contractNo);
		return contractDetailResponse.toBuilder()
				.guaranteeNameList(this.selectGuaranteeList(contractNo))
				.build();
	}
}
