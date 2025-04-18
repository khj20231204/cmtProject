package com.example.cmtProject.service.mes.production;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.cmtProject.dto.mes.manufacturingMgt.MfgScheduleDTO;
import com.example.cmtProject.dto.mes.production.WorkOrderDTO;
import com.example.cmtProject.mapper.mes.production.WorkOrderMapper;

import jakarta.transaction.Transactional;

@Service
public class WorkOrderService {
	@Autowired WorkOrderMapper orderMapper;
	//작업지시 리스트
	public List<WorkOrderDTO> getOrderList() {
		return orderMapper.selectOrderList();
	}
	//제조계획
	public List<MfgScheduleDTO> getPlanList() {
		return orderMapper.selectPlanList();
	}
	@Transactional
	public void registMsPlan(WorkOrderDTO workOrderDTO) {
		orderMapper.insertMsPlan(workOrderDTO); // 작업지시 등록
		updateMfgStatus(workOrderDTO.getMsCode()); // 제조계획 상태 변경
	}
	public void updateMfgStatus(String msCode) {
		orderMapper.updateMfgStatus(msCode);
	}
	//작업시작 버튼 = 날짜 업데이트&진행중
	public void startWork(Long workOrderNo) {
		orderMapper.updateWorkStartTime(workOrderNo);
		
	}
	
}
