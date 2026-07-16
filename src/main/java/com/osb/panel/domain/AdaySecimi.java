package com.osb.panel.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "aday_secimi", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"isveren_id", "is_arayan_id"})
})
@Getter
@Setter
@NoArgsConstructor
public class AdaySecimi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "isveren_id", nullable = false)
    private Kullanici isveren;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "is_arayan_id", nullable = false)
    private IsArayan isArayan;

    @Column(length = 500)
    private String notlar;

    @Column(name = "secim_tarihi", updatable = false)
    private LocalDateTime secimTarihi;

    @PrePersist
    protected void onCreate() {
        this.secimTarihi = LocalDateTime.now();
    }
}
