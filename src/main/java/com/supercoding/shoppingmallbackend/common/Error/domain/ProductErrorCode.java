package com.supercoding.shoppingmallbackend.common.Error.domain;

import com.supercoding.shoppingmallbackend.common.Error.ErrorCode;
import com.supercoding.shoppingmallbackend.common.Error.ErrorCodeInterface;

public enum ProductErrorCode implements ErrorCodeInterface {

    // 상품
    NOTFOUND_PRODUCT(404, "상품을 찾을 수 없습니다.");

    private final ErrorCode errorCode;

    ProductErrorCode(Integer status, String message) {
        this.errorCode = new ErrorCode(status, message) {};
    }

    @Override
    public ErrorCode getErrorCode() {
        return errorCode;
    }

}
