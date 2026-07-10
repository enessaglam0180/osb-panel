package com.osb.panel.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "is_ilani")
@Getter @Setter
public class IsIlani {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Her ilanın bir sahibi (Sanayici) olmak zorunda
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sanayici_id", nullable = false)
    private Sanayici sanayici;

    @Column(nullable = false, length = 150)
    private String baslik;

    @Column(nullable = false, length = 2000)
    private String aciklama;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Durum durum = Durum.AKTIF; // Varsayılan olarak aktif başlasın

    @Column(name = "olusturulma_tarihi", updatable = false)
    private LocalDateTime olusturulmaTarihi;

    @PrePersist
    protected void onCreate() {
        this.olusturulmaTarihi = LocalDateTime.now();
    }

    public enum Durum {
        AKTIF, PASIF
    }
}