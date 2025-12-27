package com.paypal.transaction_service.client;


import com.paypal.transaction_service.dto.dto.*;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "wallet-service", url = "http://wallet-service:8088")
public interface WalletClient {

    @PostMapping("/api/v1/wallets/hold")
    HoldResponse placeHold(@RequestBody HoldRequest request);

    @PostMapping("/api/v1/wallets/capture")
    void capture(@RequestBody CaptureRequest request);

    @PostMapping("/api/v1/wallets/credit")
    void credit(@RequestBody CreditRequest request);

    @PostMapping("/api/v1/wallets/release/{holdRef}")
    void release(@PathVariable String holdRef);
}
