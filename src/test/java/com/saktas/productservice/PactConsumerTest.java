package com.saktas.productservice;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.*;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.github.javafaker.Faker;
import com.saktas.productservice.controllers.ProductRestController;
import com.saktas.productservice.models.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest
@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "CouponServiceProvider")
public class PactConsumerTest {
    @Autowired
    private ProductRestController productRestController;

    @Pact(consumer = "ProductServiceConsumer")
    public RequestResponsePact pactCouponDetailBehaviour(PactDslWithProvider builder) {
        return builder
                .given("coupon exist", "code", "SUPERSALE")
                .uponReceiving("get coupon detail")
                .path("/couponapi/coupons/SUPERSALE")
                .willRespondWith()
                .status(200)
                .body(
                        new PactDslJsonBody().numberType("id")
                                .stringType("code", "SUPERSALE")
                                .numberType("discount", 10.000)
                                .stringType("expDate", "12/12/2026")
                )
//                .body(
//                        Objects.requireNonNull(PactDslJsonArray
//                                .arrayMinLike(2)
//                                .numberType("id")
//                                .stringType("code", "SUPERSALE")
//                                .numberType("discount", 10.000)
//                                .stringType("expDate", "12/12/2026")
//                                .closeObject())
//                )
                .toPact();

    }

    @Test
    @PactTestFor(pactMethod = "pactCouponDetailBehaviour")
    void getCreatedProduct(MockServer mockServer) {
        productRestController.setCouponServiceUri(mockServer.getUrl() + "/couponapi/coupons");

        Product creatingProduct = new Product();
        creatingProduct.setName(Faker.instance().commerce().productName());
        creatingProduct.setDescription(Faker.instance().lorem().sentence(1));
        creatingProduct.setPrice(BigDecimal.valueOf(Faker.instance().random().nextInt(1000, 9999)));
        creatingProduct.setCouponCode(Faker.instance().commerce().promotionCode());
        creatingProduct.setCouponCode("SUPERSALE");

        BigDecimal priceBeforeDiscount = creatingProduct.getPrice();

        Product product = productRestController.create(creatingProduct);
        assertThat("Coupon couldn't be executed", !priceBeforeDiscount.equals(product.getPrice()));
    }

    @Pact(consumer = "ProductServiceConsumer")
    public RequestResponsePact pactCouponDetailNotFoundBehaviour(PactDslWithProvider builder) {
        return builder.given("coupon not found", "code", "NON-PRESENT")
                .uponReceiving("get non present coupon")
                .path("/couponapi/coupons/NON-PRESENT")
                .willRespondWith()
                .status(404)
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "pactCouponDetailNotFoundBehaviour")
    void getCreatedProductWithoutCoupon(MockServer mockServer) {
        productRestController.setCouponServiceUri(mockServer.getUrl() + "/couponapi/coupons");

        Product creatingProduct = new Product();
        creatingProduct.setName(Faker.instance().commerce().productName());
        creatingProduct.setDescription(Faker.instance().lorem().sentence(1));
        creatingProduct.setPrice(BigDecimal.valueOf(Faker.instance().random().nextInt(1000, 9999)));
        creatingProduct.setCouponCode(Faker.instance().commerce().promotionCode());
        creatingProduct.setCouponCode("NON-PRESENT");

        BigDecimal priceBeforeDiscount = creatingProduct.getPrice();

        Product product = productRestController.create(creatingProduct);
        assertThat("Coupon couldn't be executed", priceBeforeDiscount.equals(product.getPrice()));
    }
}
