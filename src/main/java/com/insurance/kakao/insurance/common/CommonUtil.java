package com.insurance.kakao.insurance.common;

import java.time.LocalDate;

public class CommonUtil {
	public static String firstWordToLowerCase(String word) {
		return Character.toLowerCase(word.charAt(0)) + word.substring(1);
	}

	public static LocalDate plusMonth(LocalDate startDate, int contractPeriod){
		return startDate.plusMonths(contractPeriod).minusDays(1);
	}
}
