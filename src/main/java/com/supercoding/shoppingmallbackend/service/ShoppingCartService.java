package com.supercoding.shoppingmallbackend.service;

import com.supercoding.shoppingmallbackend.common.CommonResponse;
import com.supercoding.shoppingmallbackend.common.Error.CustomException;
import com.supercoding.shoppingmallbackend.common.Error.domain.ConsumerErrorCode;
import com.supercoding.shoppingmallbackend.common.Error.domain.GenreErrorCode;
import com.supercoding.shoppingmallbackend.common.Error.domain.ProductErrorCode;
import com.supercoding.shoppingmallbackend.common.util.ApiUtils;
import com.supercoding.shoppingmallbackend.common.util.JpaUtils;
import com.supercoding.shoppingmallbackend.dto.request.ShoppingCartItemRequest;
import com.supercoding.shoppingmallbackend.dto.response.ProductSimpleResponse;
import com.supercoding.shoppingmallbackend.dto.response.ShoppingCartItemResponse;
import com.supercoding.shoppingmallbackend.entity.Consumer;
import com.supercoding.shoppingmallbackend.entity.Genre;
import com.supercoding.shoppingmallbackend.entity.Product;
import com.supercoding.shoppingmallbackend.entity.ShoppingCart;
import com.supercoding.shoppingmallbackend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShoppingCartService {

    private final ShoppingCartRepository shoppingCartRepository;
    private final ConsumerRepository consumerRepository;
    private final ProductRepository productRepository;
    private final GenreRepository genreRepository;

    @Transactional
    public CommonResponse<ShoppingCartItemResponse> setProduct(ShoppingCartItemRequest shoppingCartItemRequest) {
        // 토큰에서 consumer id 혹은 email 파싱
        Long consumerId = 1L;

        Consumer consumer = consumerRepository.findById(consumerId).orElseThrow(
                ()->new CustomException(ConsumerErrorCode.NOT_FOUND_BY_ID)
        );
        Product product = productRepository.findById(shoppingCartItemRequest.getProductId()).orElseThrow(
                ()->new CustomException(ProductErrorCode.NOTFOUND_PRODUCT)
        );
        ProductSimpleResponse productResponse = ProductSimpleResponse.from(product, getGenre(product.getGenre().getId()));

        // consuemrId, productId로 장바구니 조회
        ShoppingCart shoppingCartItem = shoppingCartRepository.findByConsumerIdAndProductId(consumerId, shoppingCartItemRequest.getProductId()).orElse(null);

        if (shoppingCartItem == null) {
            // 장바구니에 존재하지 않다면
            ShoppingCart newData = ShoppingCart.builder()
                    .consumer(consumer)
                    .product(product)
                    .amount(shoppingCartItemRequest.getAmount())
                    .build();

            JpaUtils.managedSave(shoppingCartRepository, newData);

            ShoppingCartItemResponse createdData = ShoppingCartItemResponse.from(newData, productResponse);
            return ApiUtils.success("장바구니에 상품을 성공적으로 추가했습니다.", createdData);
        }

        // 장바구니에 이미 존재한다면
        shoppingCartItem.setAmount(shoppingCartItemRequest.getAmount());

        ShoppingCartItemResponse modifiedData = ShoppingCartItemResponse.from(shoppingCartItem, productResponse);
        return ApiUtils.success("장바구니에 담긴 상품의 수량을 성공적으로 변경하였습니다.", modifiedData);
    }

    public CommonResponse<List<ShoppingCartItemResponse>> getShoppingCart() {
        // 토큰에서 구매자 id 혹은 email 파싱
        Long consumerId = 1L;

        List<ShoppingCart> shoppingCartList = shoppingCartRepository.findAllByConsumerIdAndIsDeletedIsFalse(consumerId);

        List<ShoppingCartItemResponse> data = shoppingCartList.stream()
                .map(shoppingCart -> {
                    Product product = shoppingCart.getProduct();
                    Genre genre = getGenre(product.getGenre().getId());
                    ProductSimpleResponse productResponse = ProductSimpleResponse.from(product, genre);
                    return ShoppingCartItemResponse.from(shoppingCart, productResponse);
                })
                .collect(Collectors.toList());

        return ApiUtils.success("장바구니를 성공적으로 조회했습니다.", data);
    }

    private Genre getGenre(Long genreId) {
        return genreRepository.findById(genreId).orElseThrow(()->new CustomException(GenreErrorCode.NOT_FOUND_BY_ID));
    }
}
