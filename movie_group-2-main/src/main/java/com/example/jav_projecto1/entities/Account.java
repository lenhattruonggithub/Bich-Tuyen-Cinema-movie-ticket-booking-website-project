package com.example.jav_projecto1.entities;

import java.time.LocalDate;
import java.util.List;

import com.example.jav_projecto1.enumm.Gender;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountId;

    private String address;
    private LocalDate birthday;
    private String email;
    private String name;

    @Enumerated(EnumType.STRING)
    private Gender gender;
    private String identityCard;
    private String image;
    private String password;
    private String phoneNumber;
    private LocalDate registerDate;
    private Boolean status;
    @Column(unique = true, nullable = false)
    private String username;

    @ManyToOne
    @JoinColumn(name = "roleId")
    private Role role;

    @OneToMany(mappedBy = "account")
    private List<Invoice> invoices;

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private Member member;

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private Employee employee;

    @Override
    public String toString() {
        return null;
    }

    public void thenReturn(Account bannedAcc) {
    }
}
