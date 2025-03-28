package com.example.cmtProject.controller.erp.attendanceMgt;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.cmtProject.dto.erp.attendanceMgt.AttendDTO;
import com.example.cmtProject.dto.erp.attendanceMgt.AttendPageResponse;
import com.example.cmtProject.entity.erp.attendanceMgt.Attend;
import com.example.cmtProject.entity.erp.employees.Employees;
import com.example.cmtProject.entity.erp.employees.PrincipalDetails;
import com.example.cmtProject.mapper.erp.attendanceMgt.AttendsMapper;
import com.example.cmtProject.repository.erp.attendanceMgt.AttendRepository;
import com.example.cmtProject.repository.erp.employees.EmployeesRepository;
import com.example.cmtProject.service.erp.attendanceMgt.AttendService;
import com.example.cmtProject.service.erp.employees.EmployeesService;

@Controller
@RequestMapping("/attends")
public class AttendController {
	
	private static final Logger logger = LoggerFactory.getLogger(AttendController.class);
	
	@Autowired
    private AttendService attendService;
	
	@Autowired
	private AttendRepository attendRepository;
	
	@Autowired
	private AttendsMapper attendsMapper;

    // 출결 정보 목록 페이지 (HTML 렌더링)
    @GetMapping("/list")
    public String showAttendPage(Model model, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        
    	if (principalDetails == null) {
            return "redirect:/loginForm"; // 로그인 페이지로 리다이렉트
        }
    	// 유저정보
    	Employees loginUser = principalDetails.getUser();
    	
    	
        // 출근하면 퇴근 버튼만 보이게하기, 퇴근하면 출근 버튼만 보이게 하기
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = LocalDate.now().atTime(LocalTime.MAX);
        boolean hasCheckedIn = attendsMapper.hasCheckedInToday(loginUser.getEmpNo(), start, end);
        model.addAttribute("hasCheckedIn", hasCheckedIn);
        
        Long findLatestCheckInAtdNo = attendsMapper.findLatestCheckInAtdNo(loginUser.getEmpNo());
        boolean hasCheckedOut = true;
        if (findLatestCheckInAtdNo != null) {
            hasCheckedOut = attendsMapper.hasCheckedOutToday(findLatestCheckInAtdNo, start, end);
        }
        model.addAttribute("hasCheckedOut", hasCheckedOut);
       
        // 어드민은 모든정보 보기, 사원은 자기거만 보기
    	if (principalDetails.getAuthorities().stream().anyMatch(a -> 
        a.getAuthority().equals("ADMIN") || 
        a.getAuthority().equals("MANAGER"))) {
        // ADMIN, MANAGER는 모든 출결정보 조회
        List<AttendDTO> attendList = attendService.getAllAttends();
        model.addAttribute("attendList", attendList);
    	} else {
    	  // USER는 본인의 출결정보만 조회
    	  List<Attend> attendList = attendService.getAttendsByEmpNo(loginUser.getEmpNo());
    	  model.addAttribute("attendList", attendList);
    	}
        return "erp/attendanceMgt/attendList"; // templates/erp/attendanceMgt/attendList.html 렌더링
    }
    

    // 출결 정보 등록
    @PostMapping("/check-in")
    @ResponseBody
    public ResponseEntity<AttendDTO> createAttend(@RequestBody AttendDTO dto, @AuthenticationPrincipal PrincipalDetails principalDetails) {
    	// 로그인한 사용자의 아이디 가져오기
    	Employees loginUser = principalDetails.getUser();
    	AttendDTO DTO = attendService.saveAttend(dto, loginUser);

        return ResponseEntity.ok(DTO);
    }
    
    // 퇴근 정보 등록
    @PostMapping("/check-out")
    @ResponseBody
    public ResponseEntity<String> updateAttendLeave(@RequestBody AttendDTO dto, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        // 로그인한 사용자의 정보 가져오기
        Employees loginUser = principalDetails.getUser();
        // 퇴근 처리 서비스 호출
        Long findLatestCheckInAtdNo = attendsMapper.findLatestCheckInAtdNo(loginUser.getEmpNo());
        attendService.updateAttendLeave(dto, findLatestCheckInAtdNo);
        
        return ResponseEntity.ok("");
    }

    // 출결 정보 삭제
    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public String deleteAttend(@PathVariable Long id) {
        attendService.deleteAttend(id);
        return "redirect:/attends/attendList";
    }



//    // 특정 사원의 출결 정보 조회 (HTML 렌더링)
//    @GetMapping("/employee/{employeeId}")
//    public String getAttendsByEmployee(@PathVariable Long employeeId, Model model) {
//        List<AttendDTO> attends = attendService.getAttendsByEmployeeId(employeeId);
//        model.addAttribute("attends", attends);
//        return "erp/attendanceMgt/attendList"; // 특정 사원의 출결 정보를 렌더링
//    }
//
//    // 출결 정보 수정
//    @PostMapping("/update/{id}")
//    public String updateAttend(@PathVariable Long id, @ModelAttribute AttendDTO dto) {
//        attendService.updateAttend(id, dto);
//        return "redirect:/attends/view";
//    }
    
    
    
    
    
    
    
    
    
    
}