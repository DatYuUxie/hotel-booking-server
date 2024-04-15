package com.lakesidehotel.hotelbooking.controller;


import com.lakesidehotel.hotelbooking.response.PaymentResponse;
import com.lakesidehotel.hotelbooking.response.TransactionResponse;
import com.lakesidehotel.hotelbooking.security.VnPayConfig;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/payment")
public class PaymentController {
    // http://localhost:9192/payment/create-payment

    @GetMapping("/create-payment")
    public ResponseEntity<?> createPayment(HttpServletRequest req) throws UnsupportedEncodingException {
//        String vnp_Version = "2.1.0";
//        String vnp_Command = "pay";
//        long amount = Integer.parseInt(req.getParameter("amount")) * 100;
//        String bankCode = req.getParameter("bankCode");

        long amount = Integer.parseInt(req.getParameter("amount")) * 10000;

//        long amount  = 100000000;

        String orderType = "other";

        String vnp_TxnRef = VnPayConfig.getRandomNumber(8);
        String vnp_IpAddr = VnPayConfig.getIpAddress(req);

        String vnp_TmnCode = VnPayConfig.vnp_TmnCode;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", VnPayConfig.vnp_Version);
        vnp_Params.put("vnp_Command", VnPayConfig.vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(Math.round(amount)));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_BankCode", "NCB");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef);
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_OrderType", orderType);
        vnp_Params.put("vnp_ReturnUrl", VnPayConfig.vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);



        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = VnPayConfig.hmacSHA512(VnPayConfig.secretKey, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = VnPayConfig.vnp_PayUrl + "?" + queryUrl;


        PaymentResponse paymentResponse = new PaymentResponse();
        paymentResponse.setStatus("ok");
        paymentResponse.setMessage("Successfully");
        paymentResponse.setURL(paymentUrl);

        return ResponseEntity.status(HttpStatus.OK)
                .body(paymentResponse);


    }



    //http://localhost:9192/vnpay_jsp/vnpay_return.jsp?vnp_Amount=100000000&vnp_BankCode=NCB&vnp_BankTranNo=VNP14379605&vnp_CardType=ATM&vnp_OrderInfo=Thanh+toan+don+hang%3A45683857&vnp_PayDate=20240415164335&vnp_ResponseCode=00&vnp_TmnCode=XS0LASL1&vnp_TransactionNo=14379605&vnp_TransactionStatus=00&vnp_TxnRef=45683857&vnp_SecureHash=da3787a2af28f79a6ae6e322737e1c0898bd0fe7e8793aaa81cb8c56ac8aa2e0afab25c1c1cc58e831106ae9409e1de9a50452586fbf189d44024d2ddcc78282
    @GetMapping("/payment-info")
    public ResponseEntity<?> transaction(
            @RequestParam("vnp_Amount") String amount,
            @RequestParam("vnp_BankCode") String bankCode,
            @RequestParam("vnp_OrderInfo") String orderInfo,
            @RequestParam("vnp_ResponseCode") String responseCode
            ){
        TransactionResponse transactionResponse = new TransactionResponse();
        if(responseCode.equals("00")){
            transactionResponse.setStatus("ok");
            transactionResponse.setMessage("Successfully");
            transactionResponse.setData("");
        }
        else{
            transactionResponse.setStatus("Failed");
            transactionResponse.setMessage("Unsuccessfully");
            transactionResponse.setData("");
        }
        return ResponseEntity.ok(transactionResponse);
    }


}
