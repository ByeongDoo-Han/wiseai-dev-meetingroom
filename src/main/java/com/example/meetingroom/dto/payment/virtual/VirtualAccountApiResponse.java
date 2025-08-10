package com.example.meetingroom.dto.payment.virtual;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JacksonXmlRootElement(localName = "result")
public class VirtualAccountApiResponse {
    private String status;
    private String accountNum;
    private String bank;
}
