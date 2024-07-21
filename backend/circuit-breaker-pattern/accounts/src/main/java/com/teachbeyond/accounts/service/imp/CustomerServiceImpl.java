package com.teachbeyond.accounts.service.imp;

import com.teachbeyond.accounts.dto.AccountsDto;
import com.teachbeyond.accounts.dto.CardsDto;
import com.teachbeyond.accounts.dto.CustomerDetailsDto;
import com.teachbeyond.accounts.dto.LoansDto;
import com.teachbeyond.accounts.entity.Accounts;
import com.teachbeyond.accounts.entity.Customer;
import com.teachbeyond.accounts.exception.ResourceNotFoundException;
import com.teachbeyond.accounts.mapper.AccountsMapper;
import com.teachbeyond.accounts.mapper.CustomerMapper;
import com.teachbeyond.accounts.repository.AccountsRepository;
import com.teachbeyond.accounts.repository.CustomerRepository;
import com.teachbeyond.accounts.service.ICustomerService;
import com.teachbeyond.accounts.service.client.CardsFeignClient;
import com.teachbeyond.accounts.service.client.LoansFeignClient;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomerServiceImpl implements ICustomerService {
    private AccountsRepository accountsRepository;
    private CustomerRepository customerRepository;
    private CardsFeignClient cardsFeignClient;
    private LoansFeignClient loansFeignClient;

    /**
     * @param mobileNumber  - Input Mobile Number
     * @param correlationId
     * @return Customer Details based on a given mobileNumber
     */
    @Override
    public CustomerDetailsDto fetchCustomerDetails(String mobileNumber, String correlationId) {
        Customer customer = customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Customer", "mobileNummer", mobileNumber)
        );
        Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId()).orElseThrow(
                () -> new ResourceNotFoundException("Account", "customerId", customer.getCustomerId().toString())
        );

        CustomerDetailsDto customerDetailsDto = CustomerMapper.mapToCustomerDetailsDto(customer, new CustomerDetailsDto());
        customerDetailsDto.setAccountsDto(AccountsMapper.mapToAccountsDto(accounts, new AccountsDto()));

        ResponseEntity<LoansDto> loansDtoResponseEntity = loansFeignClient.fetchLoanDetails(correlationId, mobileNumber);
        if (null != loansDtoResponseEntity)
            customerDetailsDto.setLoansDto(loansDtoResponseEntity.getBody());

        ResponseEntity<CardsDto> cardsDtoResponseEntity = cardsFeignClient.fetchCardsDetails(correlationId, mobileNumber);
        if (null != cardsDtoResponseEntity)
            customerDetailsDto.setCardsDto(cardsDtoResponseEntity.getBody());

        return customerDetailsDto;
    }
}
