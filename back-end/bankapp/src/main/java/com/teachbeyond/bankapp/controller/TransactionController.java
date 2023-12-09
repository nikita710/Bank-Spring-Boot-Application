package com.teachbeyond.bankapp.controller;

import com.itextpdf.text.DocumentException;
import com.teachbeyond.bankapp.entity.Transaction;
import com.teachbeyond.bankapp.service.BankStatementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/bank-statement")
public class TransactionController {
    @Autowired
    private BankStatementService bankStatementService;

    //generate bank statement
    @GetMapping
    public List<Transaction> generateBankStatement(@RequestParam String accountNumber,
                                                   @RequestParam String startDate,
                                                   @RequestParam String endDate) throws DocumentException, FileNotFoundException {
        return bankStatementService.generateStatement(accountNumber, startDate, endDate);
    }
}
