package com.example.jav_projecto1.dto;

import java.sql.Timestamp;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BanMessageDTO {
    private Long accountId;
    private String accountName;
    private String message;
    private Timestamp banDate;
    private Boolean isBanned;

    @Override
    public String toString() {
        return "BanMessageDTO{" +
                "accountId=" + accountId +
                ", accountName='" + accountName + '\'' +
                ", message='" + message + '\'' +
                ", banDate=" + banDate +
                ", isBanned=" + isBanned +
                '}';
    }
}
