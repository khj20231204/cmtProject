package com.example.cmtProject.controller.erp.employees;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.cmtProject.dto.comm.CommonCodeDetailNameDTO;
import com.example.cmtProject.dto.erp.employees.EmpListPreviewDTO;
import com.example.cmtProject.dto.erp.employees.EmpRegistDTO;
import com.example.cmtProject.dto.erp.employees.searchEmpDTO;
import com.example.cmtProject.entity.erp.employees.PrincipalDetails;
import com.example.cmtProject.service.comm.CommonService;
import com.example.cmtProject.service.erp.employees.EmployeesService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/emp")
public class EmployeesController {
	@Autowired private EmployeesService empService;
	@Autowired private CommonService commonService;

	//공통코드 DetailName 불러오는 메서드
	public static void commonCodeName(Model model , CommonService commonService) {
		
		List<String> groupCodes = commonService.getAllGroupCodes();
		System.out.println("그룹코드 리스트 :::::"+groupCodes);
//		String[] groupCodes = {"GENDER","DEPT","EDUCATION","EMP_STATUS","EMP_TYPE","MARITAL","PARKING","POSITION","USER_ROLE"};
		//공통코드 추가시 "NEW_CODE" 추가
		
		Map<String, List<CommonCodeDetailNameDTO>> commonCodeMap = new HashMap<>();
		
		for(String groupCode : groupCodes) {
			commonCodeMap.put(groupCode, commonService.getCodeListByGroup(groupCode));
		}
		model.addAttribute("commonCodeMap",commonCodeMap);
	}
	
	
	@GetMapping("/")
	public String main() {
		return "erp/employees/emp/home";
	}
	
	
	/***나의 인사카드***/
	@GetMapping("/myEmplist")
	public String myEmplist(@AuthenticationPrincipal PrincipalDetails principalDetails,Model model,RedirectAttributes redirectAttributes) {
		
		if (principalDetails ==null) {
			redirectAttributes.addFlashAttribute("msg","로그인 필수!\n로그인창으로 이동합니다.");
			return "redirect:/login";
		}
		
		String empId = principalDetails.getUser().getEmpId();
		EmpRegistDTO emp = empService.getMyEmpList(empId);
		model.addAttribute("emp",emp);
		System.out.println("~~~DB값 조회"+emp); // deptNo=20, deptName=개발, positionNo=null, deptPosition=대리
		 //공통코드 가져오기
		commonCodeName(model, commonService);
		
		return "erp/employees/myEmplist";
	}
	
	/****나의 정보수정
	 * @throws Exception ****/
	@PostMapping("/myEmplist")
	@ResponseBody
	public String myEmpUpdate(
								@ModelAttribute EmpRegistDTO dto, //JSON 받아옴
								@RequestParam("empProfileFile") MultipartFile empProfileFile, //파일받는용도
						        @AuthenticationPrincipal PrincipalDetails principal,
						        Model model) throws Exception {
	    int result = empService.updateEmp(dto,empProfileFile);
	    if(result > 0) {
	    	model.addAttribute("emp", result);
	    	return "사원정보 수정 완료";
	    }
	    System.out.println("바뀐 사원정보~~~~~~~~~~"+ result);
	    return "erp/employees/myEmplist";
	}
	
	
	/***사원 인사카드 조회 SELECT***/
	@GetMapping("/emplist")
	public String empList(HttpSession session,Model model) {
		commonCodeName(model, commonService);
		
		List<EmpListPreviewDTO> empList = empService.getEmplist();
		model.addAttribute("emplist",empList);
		System.out.println(empList);
		
		//입력값 검증 및 파라미터로 활용할 객체
		searchEmpDTO searchEmpDTO = new searchEmpDTO();
		model.addAttribute("searchEmpDTO",searchEmpDTO);
		
		return "erp/employees/emplist";
	}
	
	/****사원 셀랙트박스****/
	@PostMapping("/emplist/searchEmp")
	public String searchDept(@ModelAttribute searchEmpDTO searchEmpDTO,Model model)throws Exception {
		commonCodeName(model, commonService);
		
		List<searchEmpDTO> searchDTO = empService.getSearchDept(searchEmpDTO);
		model.addAttribute("emplist",searchDTO);
		System.out.println(">>>>>>>>"+searchDTO);
		
		return "erp/employees/emplist";
	}
	
	/****사원 등록
	 * @throws Exception *****/
	@PostMapping("/empRegi")
	public String empRegist(@ModelAttribute("empRegistDTO") EmpRegistDTO empRegistDTO
							,@RequestParam("empProfileFile") MultipartFile empProfileFile //파일받는용도
							,Model model) throws Exception {
		//프로필 업로드
		int empRegi = empService.insertEmp(empRegistDTO,empProfileFile);
		
		if(empRegi > 0) {
			return "redirect:/emp/emplist";
		}
		System.out.println("직원추가 완료 : " + empRegistDTO);
		return "erp/employees/emplist";
	}
	
	/*사원 리스트 클릭시 사원 상세정보*/
	@GetMapping("/emplist/{id}")
	public String empListmodify(@PathVariable("id") String id,Model model) {
		commonCodeName(model, commonService);
		System.out.println("받은 id????"+id); //981114 받아옴!!!!!!!!!
		
//		List<EmpRegistDTO> emplist = empService.getEmpDetail(id);
		EmpRegistDTO emp = empService.getEmpDetail(id);
		model.addAttribute("emp",emp);
		System.out.println("사원 상세 정보 >>"+emp);
		return "erp/employees/emplistDetail";
	}
	
	@PostMapping("/empUpdate/{id}")
	@ResponseBody
	public String empModifyDetail(@PathVariable("id") String id,
								@ModelAttribute EmpRegistDTO dto, //JSON 받아옴
								@RequestParam("empProfileFile") MultipartFile empProfileFile, //파일받는용도
						        @AuthenticationPrincipal PrincipalDetails principal,
						        Model model) throws Exception {
		
		dto.setEmpId(id);
		System.out.println("받은 DTO와 id"+dto+id);
		int result = empService.updateEmp(dto,empProfileFile);
	    if(result > 0) {
	    	model.addAttribute("emp", result);
	    	return "사원수정완료!";
	    }
	    System.out.println("받은 DTO: " + result);
	    return "사원수정 실패";
		
	}
	
	//아이디 유효성 검사
	@PostMapping("/checkId")
	@ResponseBody
	public boolean checkId(@RequestParam("empId")String empId) {
		 System.out.println("넘어온 empId >>> " + empId);
		return empService.checkId(empId);
	}
	
	//아이디 찾기
	@GetMapping("/findId")
	public String findId() {
		
		return"erp/employees/findId";
	}
	
	
	@PostMapping("/findId")
	@ResponseBody
	public Map<String, Object> getFindId(@RequestParam Map<String, String> map) {
		String empName = map.get("empName");
		String empEmail = map.get("empEmail");
		
		String empId = empService.getEmpId(map);
		
		Map<String, Object> result = new HashMap<>();
		if(empId != null) {
			result.put("success", true);
	        result.put("empId", empId);
		} else {
			result.put("success", false);
		}
		System.out.println("아이디 찾기@@"+result);
		
		return result;
	}
	
	// 객체 -> JSON 변환 샘플
//	@Autowired
//	private ObjectMapper objectMapper;
//	
//	public String toJson() {
//		EmpListPreviewDTO dto = new EmpListPreviewDTO();
//		String result;
//		
//		try {
//			result = objectMapper.writeValueAsString(dto);
//		} catch (JsonProcessingException e) {
//			e.printStackTrace();
//		}
//		return result;
//	}
	
	
}
