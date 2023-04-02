 package com.insurance.kakao.insurance.service.insert;

 import java.util.Map;

 import org.springframework.stereotype.Service;

 import com.insurance.kakao.insurance.model.vo.CreateInsurance;

 import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InsuranceCreateService {
	private final Map<String, InsuranceCreatable> insuranceCreatableMap;

	public void create(CreateInsurance createInsurance) {
		InsuranceCreatable insuranceCreatable = insuranceCreatableMap.get(createInsurance.getServiceName());
		insuranceCreatable.validation(createInsurance);
		insuranceCreatable.create(createInsurance);
	}
}
