package com.mwu.cartService.service;

import com.mwu.cartService.DTO.OrderDTO;

public interface OrderService {

    OrderDTO placeOrder(String emailId, Long addressId, String paymentMethod, String pgName, String pgPaymentId,
                        String pgStatus, String pgResponseMessage);

}

