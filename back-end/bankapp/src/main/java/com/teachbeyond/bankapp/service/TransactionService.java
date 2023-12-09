package com.teachbeyond.bankapp.service;

import com.teachbeyond.bankapp.dto.TransactionDto;


public interface TransactionService {
    void saveTransaction(TransactionDto transactionDto);
}
