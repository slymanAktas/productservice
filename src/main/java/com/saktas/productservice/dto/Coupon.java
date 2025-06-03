package com.saktas.productservice.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Coupon {
    private String code;
    private BigDecimal discount;
    private String expDate;
}
