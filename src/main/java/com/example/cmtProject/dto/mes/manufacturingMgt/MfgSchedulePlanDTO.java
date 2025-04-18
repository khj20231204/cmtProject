package com.example.cmtProject.dto.mes.manufacturingMgt;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MfgSchedulePlanDTO {

	private String mpCode; 		   // 생산 계획 코드
	private String pdtCode; 	   // 제품 코드
	private String pdtName;		   // 제품명
	private String prcCode; 	   // 공정 코드
	private String prcName;		   // 공정명
	private String empId; 		   // 등록 직원 사번
	private String empName; 	   // 등록 직원명
 	private Long allocatedQty; 	   // 계획 수량
	private String msPriority;     // 우선순위
	private LocalDate msStartDate; // 제조 시작 예정일
    private LocalDate msEndDate;   // 제조 종료 예정일
    
}
