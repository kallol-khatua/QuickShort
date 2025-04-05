package com.quickshort.email.dto;

import com.quickshort.common.enums.MemberType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AddMemberDto {
    private String email;
    private MemberType memberType;
}
