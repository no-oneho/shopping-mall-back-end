package com.supercoding.shoppingmallbackend.controller;

import com.supercoding.shoppingmallbackend.common.CommonResponse;
import com.supercoding.shoppingmallbackend.common.util.ApiUtils;
import com.supercoding.shoppingmallbackend.dto.request.ProductFileRequest;
import com.supercoding.shoppingmallbackend.dto.request.ProductListRequest;
import com.supercoding.shoppingmallbackend.dto.request.ProductRequestBase;
import com.supercoding.shoppingmallbackend.dto.response.PaginationResponse;
import com.supercoding.shoppingmallbackend.dto.response.ProductDetailResponse;
import com.supercoding.shoppingmallbackend.dto.response.ProductListResponse;
import com.supercoding.shoppingmallbackend.security.AuthHolder;
import com.supercoding.shoppingmallbackend.service.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/product")
@Api(tags = "상품 CRUD API")
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "상품 등록", description = "상품 정보를 입력하여 product 레코드를 생성합니다.")
    @PostMapping(value = "/", consumes = "multipart/form-data")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = "multipart/form-data",
            schema = @Schema(implementation = MultipartFile.class)))
    public CommonResponse<Object> createProduct(@ModelAttribute ProductRequestBase productRequestBase,
                                                @ApiParam(value = "썸네일 이미지 파일 (선택)", required = false) @RequestPart(value = "mainImageFile", required = false) MultipartFile thumbNailFile,
                                                @ApiParam(value = "본문 이미지 파일들 (선택)", required = false) @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles
    ) {
        Long userIdx = AuthHolder.getProfileIdx();
        productService.createProductItem(productRequestBase, thumbNailFile, imageFiles, userIdx);

        return ApiUtils.success("상품 등록 성공", null);
    }

    @Operation(summary = "상품 리스트 조회", description = "여러 조건을 입력하여 여러개의 product 레코드를 조회합니다.")
    @GetMapping(value = "/")
    public CommonResponse<Object> getProductList(ProductListRequest productListRequest,
                                                 @Parameter(description = "카테고리", required = false, example = "[\"2인 전용\", \"2~4인 전용\", \"초급\", \"최상급\", \"30분 미만\", \"90분 이상\"]")
                                                 @RequestParam(required = false) List<String> category,
                                                 Pageable pageable) {
        productListRequest.setCategory(category);
        PaginationResponse<ProductListResponse> productListResponses = productService.getProductList(productListRequest, pageable);

        return ApiUtils.success("상품 리스트 조회 성공", productListResponses);
    }

    @Operation(summary = "상품 조회", description = "상품 식별값을 입력하여 단일의 product 레코드를 조회합니다.")
    @GetMapping(value = "/{product_idx}")
    public CommonResponse<Object> getProduct(@PathVariable("product_idx") Long productId) {

        ProductDetailResponse productDetailResponse = productService.getProductByProductId(productId);

        return ApiUtils.success("상품 조회 성공", productDetailResponse);
    }

    @Operation(summary = "상품 삭제", description = "상품 식별값을 입력하여 단일의 product 레코드를 삭제합니다.")
    @DeleteMapping("/{product_idx}")
    public CommonResponse<Object> deleteProduct(@PathVariable("product_idx") Long productId) {

        Long profileIdx = AuthHolder.getProfileIdx();
        productService.deleteProductByProductId(productId, profileIdx);

        return ApiUtils.success(productId + "번 상품 삭제 성공", null);
    }

    @Operation(summary = "상품 수정", description = "상품 식별값을 입력하여 단일의 product 레코드를 수정합니다.")
    @PatchMapping(value = "/{product_idx}", consumes = "multipart/form-data")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = "multipart/form-data",
            schema = @Schema(implementation = MultipartFile.class)))
    public CommonResponse<Object> updateProduct(@PathVariable("product_idx") Long productId,
                                                @ModelAttribute ProductFileRequest productFileRequest,
                                                @ApiParam(value = "썸네일 이미지 파일 (선택)") @RequestPart(value = "mainImageFile", required = false)
                                                    MultipartFile thumbNailFile,
                                                @ApiParam(value = "본문 이미지 파일들 (선택)") @RequestPart(value = "imageFiles", required = false)
                                                    List<MultipartFile> imageFiles) {
        Long profileIdx = AuthHolder.getProfileIdx();
        productService.updateProductByProductId(productId, profileIdx, productFileRequest, thumbNailFile, imageFiles);

        return ApiUtils.success(productId + "번 상품 수정 성공", null);
    }


}
