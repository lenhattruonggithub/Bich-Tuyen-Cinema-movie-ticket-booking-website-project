package com.example.jav_projecto1.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class RewardPointHistoryDTO {
    private Long rewardId;
    private Integer points;
    private LocalDateTime rewardDate;
    private String type;
}