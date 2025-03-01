package com.supercoding.shoppingmallbackend.controller;

import com.supercoding.shoppingmallbackend.common.CommonResponse;
import com.supercoding.shoppingmallbackend.common.util.ApiUtils;
import com.supercoding.shoppingmallbackend.dto.request.questions.CreateQuestionRequest;

import com.supercoding.shoppingmallbackend.dto.request.questions.UpdateQuestionRequest;
import com.supercoding.shoppingmallbackend.dto.response.questions.CreateQuestionResponse;
import com.supercoding.shoppingmallbackend.dto.response.questions.GetQuestionResponse;
import com.supercoding.shoppingmallbackend.dto.response.questions.UpdateQuestionResponse;
import com.supercoding.shoppingmallbackend.service.QuestionService;


import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/questions")
public class QuestionController {

    private final QuestionService questionService;

    @Autowired
    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }
    @ApiOperation(value = "문의 조회")
    @GetMapping("/{id}")
    public CommonResponse<Object> getQuestion(
            @PathVariable Long id) {
        GetQuestionResponse question = questionService.getQuestion(id);
        if( question == null){
            return ApiUtils.fail(404,"존재하지 않습니다.");
        }
        return ApiUtils.success("조회 완료", question);
    }


    @ApiOperation(value = "문의 작성")
    @PostMapping
    public CommonResponse<Object> createQuestion(
            @RequestBody CreateQuestionRequest request) {
        CreateQuestionResponse createdQuestion = questionService.createQuestion(request);
        return ApiUtils.success("작성 완료",createdQuestion);
    }
    @ApiOperation(value = "문의 수정")
    @PutMapping("/{id}")
    public CommonResponse<Object> updateQuestion(
            @PathVariable Long id,
            @RequestBody UpdateQuestionRequest request) {
        UpdateQuestionResponse updatedQuestion = questionService.updateQuestion(id, request);
        if( updatedQuestion == null){
            return ApiUtils.fail(404,"존재하지 않습니다.");
        }
        return ApiUtils.success("수정 완료", updatedQuestion);
    }

    @ApiOperation(value = "문의 삭제")
    @DeleteMapping("/{id}")
    public CommonResponse<Object> deleteQuestion(@PathVariable Long id) {
        questionService.deleteQuestion(id);
        return ApiUtils.success("삭제 완료", null);
    }
}
