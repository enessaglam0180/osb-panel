package com.osb.panel.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

import java.time.LocalDateTime;

@Entity
@Table(name = "sanayici")
@Getter
@Setter
@NoArgsConstructor
public class Sanayici {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Company name is required.")
    @Size(max = 120)
    @Column(nullable = false, length = 120)
    private String companyName;

    @Size(max = 11)
    @Column(unique = true, length = 11)
    private String taxNumber;

    @Size(max = 80)
    @Column(length = 80)
    private String contactPerson;

    @Size(max = 20)
    @Column(length = 20)
    private String phone;

    @Email(message = "Invalid email format.")
    @Size(max = 100)
    @Column(length = 100)
    private String email;

    @Column(length = 60)
    private String sector;

    @Column
    private Integer plotSizeM2;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Durum status = Durum.ACTIVE;

    // 🚀 YENİ EKLENEN KISIM: Firmanın açtığı ilanların listesi
    @OneToMany(mappedBy = "sanayici", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<IsIlani> ilanlar;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum Durum {
        ACTIVE, PASSIVE, SUSPENDED
    }
}