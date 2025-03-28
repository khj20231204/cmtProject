package com.example.cmtProject.entity.erp.salaries;

import java.time.LocalDate;

import com.example.cmtProject.dto.erp.salaries.PaymentDTO;
import com.example.cmtProject.dto.erp.salaries.SalaryItemDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "PAYMENT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayMent { // 급여 지급 이력 엔티티

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PAY_NO")
    private Long payNo; // 지급 번호

    @Column(name = "EMP_NO", nullable = false)
    private Long empNo; // 사원 번호

    @Column(name = "PAY_DATE", nullable = false)
    private LocalDate payDate; // 지급일

    @Column(name = "PAY_BASIC", nullable = false)
    private Long payBasic; // 기본급

    @Column(name = "PAY_BONUS_OVERTIME")
    private Long payBonusOvertime; // 야근수당

    @Column(name = "PAY_BONUS_TECH")
    private Long payBonusTech; // 기술수당

    @Column(name = "PAY_BONUS_LONG")
    private Long payBonusLong; // 근속수당

    @Column(name = "PAY_BONUS_COMMITION")
    private Long payBonusCommition; // 성과급

    @Column(name = "PAY_BONUS_HOLIDAY")
    private Long payBonusHoliday; // 명절수당

    @Column(name = "PAY_BONUS_TOTAL", nullable = false)
    private Long payBonusTotal; // 총수당금액

    @Column(name = "PAY_TAX_PENSION")
    private Long payTaxPension; // 국민연금

    @Column(name = "PAY_TAX_CARE")
    private Long payTaxCare; // 장기요양보험

    @Column(name = "PAY_TAX_HEALTH")
    private Long payTaxHealth; // 건강보험

    @Column(name = "PAY_TAX_EMPLOYMENT")
    private Long payTaxEmployment; // 고용보험

    @Column(name = "PAY_TAX_INCOME")
    private Long payTaxIncome; // 소득세

    @Column(name = "PAY_TAX_RESIDENCE")
    private Long payTaxResidence; // 주민세

    @Column(name = "PAY_TAX_TOTAL", nullable = false)
    private Long payTaxTotal; // 총공제금액

    @Column(name = "PAY_TOTAL", nullable = false)
    private Long payTotal; // 실수령액

    @Column(name = "PAY_STATUS", length = 50, nullable = false)
    private String payStatus;  // 지급 상태
    
    
    public PaymentDTO toDto() {
    	return PaymentDTO.builder()
    			.payNo(payNo)
				.empNo(empNo)
				.payDate(payDate)
				.payBasic(payBasic)
				.payBonusOvertime(payBonusOvertime)
				.payBonusTech(payBonusTech)
				.payBonusLong(payBonusLong)
				.payBonusCommition(payBonusCommition)
				.payBonusHoliday(payBonusHoliday)
				.payBonusTotal(payBonusTotal)
				.payTaxPension(payTaxPension)
				.payTaxCare(payTaxCare)
				.payTaxHealth(payTaxHealth)
				.payTaxEmployment(payTaxEmployment)
				.payTaxIncome(payTaxIncome)
				.payTaxResidence(payTaxResidence)
				.payTaxTotal(payTaxTotal)
				.payTotal(payTotal)
				.payStatus(payStatus)
				.build();
    }
}
