package com.teachbeyond.bankapp.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class AccountInfo {
    private String accountName;
    private String accountNumber;
    private BigDecimal accountBalance;
}
