package com.teachbeyond.bankapp.utils;

import com.teachbeyond.bankapp.dto.AccountInfo;
import com.teachbeyond.bankapp.dto.BankResponse;
import com.teachbeyond.bankapp.entity.User;
import com.teachbeyond.bankapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Year;

public class AccountUtils {
    public static final String ACCOUNT_EXISTS_CODE = "001";
    public static final String ACCOUNT_EXISTS_MESSAGE = "This user already has an account created!";
    public static final String ACCOUNT_SUCCESS_CODE = "002";
    public static final String ACCOUNT_SUCCESS_MESSAGE = "Account created successfully.";
    public static final String ACCOUNT_NOT_EXIST_CODE = "003";
    public static final String ACCOUNT_NOT_EXIST_MESSAGE = "User with the provided Account Number does not exists!";
    public static final String ACCOUNT_FOUND_CODE = "004";
    public static final String ACCOUNT_FOUND_MESSAGE = "User Account Found.";
    public static final String ACCOUNT_CREDIT_SUCCESS_CODE = "005";
    public static final String ACCOUNT_CREDIT_SUCCESS_MESSAGE = "User Account Credited Successfully.";

    public static final String INSUFFICIENT_BALANCE_CODE = "006";
    public static final String INSUFFICIENT_BALANCE_MESSAGE = "Insufficient Balance.";
    public static final String ACCOUNT_DEBIT_SUCCESS_CODE = "007";
    public static final String ACCOUNT_DEBIT_SUCCESS_MESSAGE = "User Account Debited Successfully.";

    public static final String TRANSFER_SUCCESSFUL_CODE = "008";
    public static final String TRANSFER_SUCCESSFUL_MESSAGE = "Transfer Success.";

    @Autowired
    private static UserRepository userRepository;

    // Generate Account Number
    public static String generateAccountNumber() {

        /**
         * Current Year + Rando Six Digits
         */
        Year currentYear = Year.now();
        int min = 100000;
        int max = 999999;

        //generate a random number between min and max
        int generateRandomNumber = (int) (Math.random() * (max - min + 1) + min);

        //concat current year and random number
        String randomNumber = String.valueOf(generateRandomNumber);
        String year = String.valueOf(currentYear);

        return year + randomNumber;
    }

    // Bank Response If Account Not Exists
    public static BankResponse accountNotExistResponse(String accountNumber) {
        return BankResponse.builder()
                .responseCode(ACCOUNT_NOT_EXIST_CODE)
                .responseMessage(ACCOUNT_NOT_EXIST_MESSAGE)
                .accountInfo(null)
                .build();
    }

    // Get Account Info
    public static AccountInfo getAccountInfo(User user) {
        return AccountInfo.builder()
                .accountNumber(user.getAccountNumber())
                .accountBalance(user.getAccountBalance())
                .accountName(user.getFirstName() + " " + user.getOtherName() + " " + user.getLastName())
                .build();
    }
}
