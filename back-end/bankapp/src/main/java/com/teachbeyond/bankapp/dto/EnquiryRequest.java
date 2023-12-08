package com.teachbeyond.bankapp.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class EnquiryRequest {
    private String accountNumber;
}
