package com.quickshort.payment.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RazorpayService {
    @Value("${razorpay.api.key}")
    private String apiKey;

    @Value("${razorpay.api.secret}")
    private String apiSecret;

    public String createOrder(double amount, String receipt) throws RazorpayException {
        RazorpayClient razorpay = new RazorpayClient(apiKey, apiSecret);

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", (int) (amount * 100)); // Amount in paise
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", receipt); // Adding receipt
        orderRequest.put("payment_capture", 1); // Auto capture payment

        Order order = razorpay.orders.create(orderRequest);
        return order.get("id");
    }

    public boolean verifyPayment(String paymentId, String orderId, String signature) throws RazorpayException {
        RazorpayClient razorpayClient = new RazorpayClient(apiKey, apiSecret);

        JSONObject attributes = new JSONObject();
        attributes.put("razorpay_payment_id", paymentId);
        attributes.put("razorpay_order_id", orderId);
        attributes.put("razorpay_signature", signature);

        return Utils.verifyPaymentSignature(attributes, apiSecret);
    }
}
