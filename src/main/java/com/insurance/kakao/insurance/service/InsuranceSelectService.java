package com.insurance.kakao.insurance.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
		List<ProductResponse> productList = selectAllProductList();
		Optional<ProductResponse> findProduct = productList.stream()
				.filter(v -> v.getProductNo() == productNo)
				.findFirst();

		if(findProduct.isEmpty()){
			throw new BusinessErrorCodeException(ErrorCode.SELECT_PRODUCT);
		}
		return findProduct.get();
	}

	public List<ProductResponse> selectAllProductList(){
		return query.selectAllProductList();
	}

	public List<Integer> selectGuaranteeNoList(int contractNo){
		List<Integer> guaranteeNoList = query.selectGuaranteeNoList(contractNo);
		if(CollectionUtils.isEmpty(guaranteeNoList)){
			throw new BusinessErrorCodeException(ErrorCode.SELECT_GUARANTEE_NO_LIST);
		}
		return guaranteeNoList;
	}

	public List<GuaranteeResponse> selectGuaranteeList(int contractNo){
		List<Integer> guaranteeNoList = this.selectGuaranteeNoList(contractNo);
		return this.selectGuaranteeList(guaranteeNoList);
	}

	public List<GuaranteeResponse> selectGuaranteeList(List<Integer> guaranteeNoList){
		List<GuaranteeResponse> allGuaranteeList = selectAllGuaranteeList();
		List<GuaranteeResponse> guaranteeList = allGuaranteeList.stream()
				.filter(v -> guaranteeNoList.contains(v.getGuaranteeNo()))
				.collect(Collectors.toList());

		if(CollectionUtils.isEmpty(guaranteeList)){
			throw new BusinessErrorCodeException(ErrorCode.SELECT_GUARANTEE_LIST);
		}

		return guaranteeList;
	}

	public List<GuaranteeResponse> selectAllGuaranteeList(){
		return query.selectAllGuaranteeList();
	}

	public double getTotalAmount(int contractNo){
		List<GuaranteeResponse> guaranteeList = this.selectGuaranteeList(contractNo);
		int contractPeriod = this.getContractInfo(contractNo).getContractPeriod();
		return  getTotalAmount(guaranteeList, contractPeriod);
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
				.guaranteeNameList(this.selectGuaranteeList(contractNo))
				.build();
	}

	public List<Integer> selectGuaranteeMappingList(int productNo){
		List<Integer> mappingList = query.selectGuaranteeMappingList(productNo);
		if(CollectionUtils.isEmpty(mappingList)){
			throw new BusinessErrorCodeException(ErrorCode.SELECT_GUARANTEE_MAPPING_LIST);
		}
		return mappingList;
	}

	public long getNotExistGuaranteeCount(int productNo, List<Integer> guaranteeNoList){
		List<Integer> guaranteeMappingList = this.selectGuaranteeMappingList(productNo);
		return guaranteeNoList.stream()
				.filter(v -> !guaranteeMappingList.contains(v))
				.count();
	}
}
