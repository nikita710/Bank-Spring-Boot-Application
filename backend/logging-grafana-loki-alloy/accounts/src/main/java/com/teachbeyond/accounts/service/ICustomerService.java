package com.teachbeyond.accounts.service;

import com.teachbeyond.accounts.dto.CustomerDetailsDto;

public interface ICustomerService {
    /**
     * @param mobileNumber  - Input Mobile Number
     * @param correlationId
     * @return Customer Details based on a given mobileNumber
     */
    CustomerDetailsDto fetchCustomerDetails(String mobileNumber, String correlationId);
}
