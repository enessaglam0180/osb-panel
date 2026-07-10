package com.osb.panel.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "is_arayan")
@Getter @Setter
public class IsArayan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ad_soyad", nullable = false, length = 100)
    private String adSoyad;

    @Column(nullable = false, length = 100, unique = true)
    private String eposta;

    @Column(length = 20)
    private String telefon;

    @Column(length = 100)
    private String meslek;

    // KRİTİK: CV dosyası veritabanında değil, sunucuda duracak. Burada sadece dosya adı/yolu olacak.
    @Column(name = "cv_dosya_yolu", length = 255)
    private String cvDosyaYolu;

    @Column(name = "kayit_tarihi", updatable = false)
    private LocalDateTime kayitTarihi;

    @PrePersist
    protected void onCreate() {
        this.kayitTarihi = LocalDateTime.now();
    }
}