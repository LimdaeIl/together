package com.book.together.auth.entity;

import com.book.together.common.audit.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "v1_members")
@Entity
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "password", nullable = false, length = 1024)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "member_role", nullable = false)
    private MemberRole memberRole = MemberRole.USER;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "company_name", nullable = false, length = 50)
    private String companyName;

    @Column(name = "image", length = 256)
    private String image;

    @Builder(access = AccessLevel.PACKAGE)
    private Member(String email, String password, String name, String companyName) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.companyName = companyName;
    }

    public static Member of(String email, String password, String name, String companyName) {
        return Member.builder()
                .email(email)
                .password(password)
                .name(name)
                .companyName(companyName)
                .build();
    }
}
