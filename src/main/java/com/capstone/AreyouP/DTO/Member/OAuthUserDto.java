package com.capstone.AreyouP.DTO.Member;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Setter
@Getter
public class OAuthUserDto {
    private Long id;
    private String name;
    private String email;
    private Date created;
    private List<String> roles= new ArrayList<>();
}
