package com.github.sbcharr.user_service.dtos;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EmailDto {
    String to;
    String from;
    String subject;
    String body;
}
