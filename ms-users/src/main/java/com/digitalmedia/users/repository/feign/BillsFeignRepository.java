package com.digitalmedia.users.repository.feign;

import com.digitalmedia.users.model.Bill;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class BillsFeignRepository {

    private final BillsFeignClient feignClient;

    public List<Bill> getCustomerBill(String id){
        return feignClient.getCustomerBill(id);
    }

}
