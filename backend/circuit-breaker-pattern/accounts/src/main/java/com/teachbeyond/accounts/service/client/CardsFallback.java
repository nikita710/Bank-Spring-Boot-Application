package com.teachbeyond.accounts.service.client;

import com.teachbeyond.accounts.dto.CardsDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class CardsFallback implements CardsFeignClient {
    /**
     * @param correlationId
     * @param mobileNumber
     * @return
     */
    @Override
    public ResponseEntity<CardsDto> fetchCardsDetails(String correlationId, String mobileNumber) {
        return null;
    }
}
