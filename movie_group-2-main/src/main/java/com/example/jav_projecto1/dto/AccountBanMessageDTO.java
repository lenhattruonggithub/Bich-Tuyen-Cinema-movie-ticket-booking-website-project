package com.example.jav_projecto1.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountBanMessageDTO {
    private List<AccountDTO> accounts;
    private List<BanMessageDTO> bans;
}
