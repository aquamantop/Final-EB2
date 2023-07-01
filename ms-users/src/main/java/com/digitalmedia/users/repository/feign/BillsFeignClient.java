package com.digitalmedia.users.repository.feign;

import com.digitalmedia.users.feign.AccessTokenInterceptor;
import com.digitalmedia.users.model.Bill;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@FeignClient(name = "ms-bills", url = "http://localhost:8085/", configuration = AccessTokenInterceptor.class)
public interface BillsFeignClient {

    @GetMapping("/bills/get/{customerBill}")
    List<Bill> getCustomerBill(@PathVariable String customerBill);

}
