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
		InsuranceCreatable creatable = insuranceCreatableMap.get(createInsurance.getServiceName());
		creatable.create(createInsurance);
	}
}
