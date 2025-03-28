package com.example.cmtProject.comm.response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ModelAndViewMethodReturnValueHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * MVC 컨트롤러의 Model 데이터 변환 처리
 * 모델에 담긴 데이터를 뷰로 전달하기 전에 필드명 변환
 */
@Component
@Slf4j
public class ModelTransformer implements HandlerMethodReturnValueHandler {

    private final ModelAndViewMethodReturnValueHandler delegate;
    private final ObjectMapper objectMapper;
    private final TransformationHelper transformHelper;
    
    public ModelTransformer(ObjectMapper objectMapper, TransformationHelper transformHelper) {
        this.delegate = new ModelAndViewMethodReturnValueHandler();
        this.objectMapper = objectMapper;
        this.transformHelper = transformHelper;
        log.info("ModelTransformer 초기화 완료");
    }
    
    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        // String(뷰 이름)과 ModelAndView 반환 지원
        return String.class.isAssignableFrom(returnType.getParameterType()) ||
               ModelAndView.class.isAssignableFrom(returnType.getParameterType());
    }
    
    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, 
                                 ModelAndViewContainer mavContainer, NativeWebRequest webRequest) {
        
        // 모델 속성 변환 - 모든 모델 데이터는 무조건 변환
        Map<String, Object> model = mavContainer.getModel();
        if (model != null && !model.isEmpty()) {
            try {
                transformModelAttributes(model);
                log.debug("모델 데이터 변환 완료: {}", returnType.getExecutable());
            } catch (Exception e) {
                log.error("모델 데이터 변환 실패: {}", e.getMessage(), e);
            }
        }
        
        try {
            delegate.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
        } catch (Exception e) {
            log.error("ModelAndView 처리 오류: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Model의 모든 속성을 변환
     */
    @SuppressWarnings("unchecked")
    private void transformModelAttributes(Map<String, Object> model) {
        if (model == null || model.isEmpty()) return;
        
        Map<String, Object> transformedModel = new HashMap<>();
        
        for (Map.Entry<String, Object> entry : model.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            
            // Model 인터페이스 자체는 변환하지 않음
            if (value instanceof Model) {
                transformedModel.put(key, value);
                continue;
            }
            
            if (value instanceof Map) {
                // Map 타입 변환
                transformedModel.put(key, transformHelper.transformMapFromInternalToExternal((Map<String, Object>) value));
            } else if (value instanceof List) {
                // 리스트 타입 변환
                List<?> list = (List<?>) value;
                if (!list.isEmpty()) {
                    if (list.get(0) instanceof Map) {
                        // Map 리스트 변환
                        List<Map<String, Object>> transformedList = new ArrayList<>();
                        for (Object item : list) {
                            transformedList.add(transformHelper.transformMapFromInternalToExternal((Map<String, Object>) item));
                        }
                        transformedModel.put(key, transformedList);
                    } else if (!(list.get(0) instanceof String) && 
                               !(list.get(0) instanceof Number) && 
                               !(list.get(0) instanceof Boolean)) {
                        // DTO 객체 리스트 변환
                        List<Map<String, Object>> transformedList = new ArrayList<>();
                        for (Object item : list) {
                            try {
                                Map<String, Object> itemMap = objectMapper.convertValue(item, Map.class);
                                transformedList.add(transformHelper.transformMapFromInternalToExternal(itemMap));
                            } catch (Exception e) {
                                // 변환 실패 시 원본 유지
                                log.warn("객체 변환 실패: {}", e.getMessage());
                                transformedList.add(Map.of("originalObject", item));
                            }
                        }
                        transformedModel.put(key, transformedList);
                    } else {
                        // 기본 타입 리스트는 변환하지 않음
                        transformedModel.put(key, value);
                    }
                } else {
                    // 빈 리스트는 변환하지 않음
                    transformedModel.put(key, value);
                }
            } else if (value != null && 
                      !(value instanceof String) && 
                      !(value instanceof Number) && 
                      !(value instanceof Boolean)) {
                try {
                    // DTO 객체 변환
                    Map<String, Object> valueMap = objectMapper.convertValue(value, Map.class);
                    transformedModel.put(key, transformHelper.transformMapFromInternalToExternal(valueMap));
                } catch (Exception e) {
                    // 변환 실패 시 원본 유지
                    log.warn("객체 변환 실패: {}", e.getMessage());
                    transformedModel.put(key, value);
                }
            } else {
                // 기본 타입은 변환하지 않음
                transformedModel.put(key, value);
            }
        }
        
        // 원래 모델 비우고 변환된 속성으로 채우기
        model.clear();
        model.putAll(transformedModel);
    }
}