package com.teachbeyond.bankapp.service;

import com.teachbeyond.bankapp.dto.*;
import com.teachbeyond.bankapp.entity.User;
import com.teachbeyond.bankapp.repository.UserRepository;
import com.teachbeyond.bankapp.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Transactional
    @Override
    public BankResponse createAccount(UserRequest userRequest) {
        /**
         * creating an account - saving a new user into the db
         * check if user already has an account
         */

        if (userRepository.existsByEmail(userRequest.getEmail())) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User newUser = User.builder()
                .firstName(userRequest.getFirstName())
                .otherName(userRequest.getOtherName())
                .lastName(userRequest.getLastName())
                .gender(userRequest.getGender())
                .address(userRequest.getAddress())
                .stateOfOrigin(userRequest.getStateOfOrigin())
                .accountNumber(AccountUtils.generateAccountNumber())
                .email(userRequest.getEmail())
                .accountBalance(BigDecimal.ZERO)
                .phoneNumber(userRequest.getPhoneNumber())
                .status("ACTIVE")
                .build();

        User savedUser = userRepository.save(newUser);

        // Send email notification
        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(savedUser.getEmail())
                .subject("ACCOUNT CREATION")
                .messageBody("Congratulations! Your Account Has Been Successfully Created. \n Your Account Details: \n" +
                        "Account Name : " + savedUser.getFirstName() + " " + savedUser.getOtherName() + " " + savedUser.getLastName() + "\nAccount Number : "
                        + savedUser.getAccountNumber())
                .build();
        emailService.sendEmailAlert(emailDetails);

        // Get Created Account Info
        AccountInfo accountInfo = AccountInfo.builder()
                .accountNumber(savedUser.getAccountNumber())
                .accountBalance(savedUser.getAccountBalance())
                .accountName(savedUser.getFirstName() + " " + savedUser.getOtherName() + " " + savedUser.getLastName())
                .build();

        //After Successfully Created Account Send Bank Response To User
        BankResponse bankResponse = BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_SUCCESS_CODE)
                .responseMessage(AccountUtils.ACCOUNT_SUCCESS_MESSAGE)
                .accountInfo(accountInfo)
                .build();
        return bankResponse;
    }

    @Override
    public BankResponse balanceEnquiry(EnquiryRequest enquiryRequest) {
        //Check if the provided account number is existing in database
        Boolean isAccountExist = userRepository.existsByAccountNumber(enquiryRequest.getAccountNumber());
        if (!isAccountExist) {
            return AccountUtils.accountNotExistResponse(enquiryRequest.getAccountNumber());
        }

        //Account found
        User foundUser = userRepository.findByAccountNumber(enquiryRequest.getAccountNumber());
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_FOUND_CODE)
                .responseMessage(AccountUtils.ACCOUNT_FOUND_MESSAGE)
                .accountInfo(AccountUtils.getAccountInfo(foundUser))
                .build();
    }

    @Override
    public String nameEnquiry(EnquiryRequest enquiryRequest) {
        //Check User Account is exist or not
        Boolean isAccountExist = userRepository.existsByAccountNumber(enquiryRequest.getAccountNumber());
        if (!isAccountExist) {
            return AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE;
        }
        User foundUser = userRepository.findByAccountNumber(enquiryRequest.getAccountNumber());
        return foundUser.getFirstName() + " " + foundUser.getOtherName() + " " + foundUser.getLastName();
    }

    @Override
    public BankResponse creditAccount(CreditDebitRequest creditRequest) {
        //Check User Account is exist or not
        Boolean isAccountExist = userRepository.existsByAccountNumber(creditRequest.getAccountNumber());
        if (!isAccountExist) {
            return AccountUtils.accountNotExistResponse(creditRequest.getAccountNumber());
        }

        User userToCredit = userRepository.findByAccountNumber(creditRequest.getAccountNumber());
        userToCredit.setAccountBalance(userToCredit.getAccountBalance().add(creditRequest.getAmount()));
        userRepository.save(userToCredit);

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREDIT_SUCCESS_CODE)
                .responseMessage(AccountUtils.ACCOUNT_CREDIT_SUCCESS_MESSAGE)
                .accountInfo(AccountUtils.getAccountInfo(userToCredit))
                .build();
    }

    @Override
    public BankResponse debitAccount(CreditDebitRequest debitRequest) {
        Boolean isAccountExist = userRepository.existsByAccountNumber(debitRequest.getAccountNumber());
        if (!isAccountExist) {
            return AccountUtils.accountNotExistResponse(debitRequest.getAccountNumber());
        }

        User userToDebit = userRepository.findByAccountNumber(debitRequest.getAccountNumber());

        BigDecimal accountBalance = userToDebit.getAccountBalance();
        BigDecimal debitAmount = debitRequest.getAmount();
        if (accountBalance.intValue() < debitAmount.intValue()) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(AccountUtils.getAccountInfo(userToDebit))
                    .build();
        } else {
            userToDebit.setAccountBalance(userToDebit.getAccountBalance().subtract(debitRequest.getAmount()));
            userRepository.save(userToDebit);
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_DEBIT_SUCCESS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_DEBIT_SUCCESS_MESSAGE)
                    .accountInfo(AccountUtils.getAccountInfo(userToDebit))
                    .build();
        }

    }

    @Transactional
    @Override
    public BankResponse transfer(TransferRequest transferRequest) {
        //get the account to debit (check it is existing)
        boolean isSourceAccountExist = userRepository.existsByAccountNumber(transferRequest.getSourceAccountNumber());
        boolean isDestinationAccountExist = userRepository.existsByAccountNumber(transferRequest.getDestinationAccountNumber());
        if (!isDestinationAccountExist || !isSourceAccountExist) {
            return AccountUtils.accountNotExistResponse(transferRequest.getDestinationAccountNumber());
        }

        // Debit Source Account Holder
        User sourceAccountUser = userRepository.findByAccountNumber(transferRequest.getSourceAccountNumber());
        if (transferRequest.getAmount().compareTo(sourceAccountUser.getAccountBalance()) > 0) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(AccountUtils.getAccountInfo(sourceAccountUser))
                    .build();
        }
        sourceAccountUser.setAccountBalance(sourceAccountUser.getAccountBalance().subtract(transferRequest.getAmount()));
        userRepository.save(sourceAccountUser);

        // Credit Destination Account Holder
        User destinationAccountUser = userRepository.findByAccountNumber(transferRequest.getDestinationAccountNumber());
        destinationAccountUser.setAccountBalance(destinationAccountUser.getAccountBalance().add(transferRequest.getAmount()));
        userRepository.save(destinationAccountUser);


        //Send Email Notification Of Debit
        EmailDetails debitAlert = EmailDetails.builder()
                .subject("DEBIT ALERT")
                .recipient(sourceAccountUser.getEmail())
                .messageBody("The transfer of " + transferRequest.getAmount() + " to account number " + destinationAccountUser.getAccountNumber() + " was successful !\n"
                        + " Your available balance is now : " + sourceAccountUser.getAccountBalance())
                .build();
        emailService.sendEmailAlert(debitAlert);

        //Send Email Notification Of Credit
        EmailDetails creditAlert = EmailDetails.builder()
                .subject("CREDIT ALERT")
                .recipient(destinationAccountUser.getEmail())
                .messageBody("The amount of " + transferRequest.getAmount() + " has been credited to your account from account number " + sourceAccountUser.getAccountNumber() + " successfully !\n"
                        + "Your available balance is now " + destinationAccountUser.getAccountBalance())
                .build();
        emailService.sendEmailAlert(creditAlert);

        //Success Bank Transfer Response
        return BankResponse.builder()
                .responseCode(AccountUtils.TRANSFER_SUCCESSFUL_CODE)
                .responseMessage(AccountUtils.TRANSFER_SUCCESSFUL_MESSAGE)
                .accountInfo(null)
                .build();
    }
}
