package com.teamproject.account.member;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberNo;

    @Column(length = 20,unique = true)
    private String username;

    @Column(length=16)
    private String password;

    @Column(length=50)
    private String memberName;

    private String email;

    private String memberFile;

}