package com.teachbeyond.bankapp.service;

import com.teachbeyond.bankapp.dto.EmailDetails;

public interface EmailService {
    void sendEmailAlert(EmailDetails emailDetails);
}
