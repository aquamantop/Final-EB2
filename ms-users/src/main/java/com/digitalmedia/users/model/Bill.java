package com.digitalmedia.users.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Bill {

    private String idBill;

    private String customerBill;

    private String productBill;

    private Double totalPrice;
}