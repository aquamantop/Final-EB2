package com.dh.msbills.controllers;

import com.dh.msbills.models.Bill;
import com.dh.msbills.services.BillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/bills")
@RequiredArgsConstructor
public class BillController {

    private final BillService service;

    @GetMapping("/all")
    public ResponseEntity<List<Bill>> getAll() {
        return ResponseEntity.ok().body(service.getAllBill());
    }

    @PostMapping("/add")
    public ResponseEntity<Bill> add(@RequestBody Bill bill){
        return ResponseEntity.ok().body(service.add(bill));
    }

    @GetMapping("/get/{customerBill}")
    public ResponseEntity<List<Bill>> getCustomerBill(@PathVariable String customerBill){
        return ResponseEntity.ok().body(service.getCustomerBill(customerBill));
    }

}
