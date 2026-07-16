package com.osb.panel.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "kullanici")
@Getter
@Setter
@NoArgsConstructor
public class Kullanici {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Kullanıcı adı zorunludur.")
    @Size(max = 50)
    @Column(name = "kullanici_adi", nullable = false, unique = true, length = 50)
    private String kullaniciAdi;

    @NotBlank(message = "Şifre zorunludur.")
    @Column(nullable = false, length = 255)
    private String sifre;

    @Size(max = 100)
    @Column(name = "ad_soyad", length = 100)
    private String adSoyad;

    @Size(max = 100)
    @Column(length = 100)
    private String eposta;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Rol rol = Rol.ISVEREN;

    @Column(nullable = false)
    private boolean aktif = true;

    @Column(name = "olusturulma_tarihi", updatable = false)
    private LocalDateTime olusturulmaTarihi;

    @PrePersist
    protected void onCreate() {
        this.olusturulmaTarihi = LocalDateTime.now();
    }

    public enum Rol {
        OPERATOR,
        ISVEREN
    }
}
