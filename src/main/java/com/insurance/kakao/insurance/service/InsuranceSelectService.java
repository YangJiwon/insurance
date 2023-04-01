package com.insurance.kakao.insurance.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import com.insurance.kakao.insurance.common.exception.BusinessErrorCodeException;
import com.insurance.kakao.insurance.common.exception.ErrorCode;
import com.insurance.kakao.insurance.mapper.InsuranceQueryMapper;
import com.insurance.kakao.insurance.model.response.ContractDetailResponse;
import com.insurance.kakao.insurance.model.response.ContractResponse;
import com.insurance.kakao.insurance.model.response.GuaranteeResponse;
import com.insurance.kakao.insurance.model.response.ProductResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InsuranceSelectService {
	private final InsuranceQueryMapper query;

	public ProductResponse getProductInfo(int productNo){
		ProductResponse productInfo = query.getProductInfo(productNo);
		if(ObjectUtils.isEmpty(productInfo)){
			throw new BusinessErrorCodeException(ErrorCode.SELECT_PRODUCT);
		}
		return productInfo;
	}

	public List<Integer> selectContractGuaranteeMappingNoList(int contractNo){
		List<Integer> contractGuaranteeMappingNoList = query.selectContractGuaranteeMappingNoList(contractNo);
		if(CollectionUtils.isEmpty(contractGuaranteeMappingNoList)){
			throw new BusinessErrorCodeException(ErrorCode.SELECT_CONTRACT_GUARANTEE_MAPPING_NO_LIST);
		}
		return contractGuaranteeMappingNoList;
	}

	public List<GuaranteeResponse> selectContractGuaranteeList(int contractNo){
		List<Integer> contractGuaranteeMappingNoList = this.selectContractGuaranteeMappingNoList(contractNo);
		return this.selectGuaranteeList(contractGuaranteeMappingNoList);
	}

	public List<GuaranteeResponse> selectGuaranteeList(List<Integer> guaranteeNoList){
		List<GuaranteeResponse> guaranteeList = query.selectGuaranteeList(guaranteeNoList);
		if(CollectionUtils.isEmpty(guaranteeList)){
			throw new BusinessErrorCodeException(ErrorCode.SELECT_GUARANTEE_LIST);
		}

		return guaranteeList;
	}

	public double getTotalAmount(int contractNo){
		List<GuaranteeResponse> guaranteeList = this.selectContractGuaranteeList(contractNo);
		int contractPeriod = this.getContractInfo(contractNo).getContractPeriod();
		return getTotalAmount(guaranteeList, contractPeriod);
	}

	public double getTotalAmount(List<GuaranteeResponse> guaranteeList, int contractPeriod){
		double totalSum = guaranteeList.stream()
				.map(GuaranteeResponse::getPaymentAmount)
				.reduce(0d, Double::sum);
		return  Math.floor(contractPeriod * totalSum * 100.0) / 100.0;
	}

	public ContractResponse getContractInfo(int contractNo){
		ContractResponse contractResponse = query.getContractInfo(contractNo);
		if(ObjectUtils.isEmpty(contractResponse)){
			throw new BusinessErrorCodeException(ErrorCode.SELECT_CONTRACT);
		}
		return contractResponse;
	}

	public ContractDetailResponse getContractDetail(int contractNo){
		ContractDetailResponse contractDetailResponse = query.getContractDetail(contractNo);
		if(ObjectUtils.isEmpty(contractDetailResponse)){
			throw new BusinessErrorCodeException(ErrorCode.SELECT_CONTRACT);
		}

		return contractDetailResponse.toBuilder()
				.guaranteeNameList(this.selectContractGuaranteeList(contractNo))
				.build();
	}

	public List<Integer> selectProductGuaranteeMappingList(int productNo){
		List<Integer> mappingList = query.selectProductGuaranteeMappingList(productNo);
		if(CollectionUtils.isEmpty(mappingList)){
			throw new BusinessErrorCodeException(ErrorCode.SELECT_PRODUCT_GUARANTEE_MAPPING_LIST);
		}
		return mappingList;
	}

	public long notExistGuaranteeCount(int productNo, List<Integer> guaranteeNoList){
		List<Integer> productGuaranteeMappingList = this.selectProductGuaranteeMappingList(productNo);
		return guaranteeNoList.stream()
				.filter(v -> !productGuaranteeMappingList.contains(v))
				.count();
	}
}
