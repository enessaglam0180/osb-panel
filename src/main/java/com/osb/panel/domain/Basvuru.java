package com.osb.panel.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "basvuru")
@Getter @Setter
public class Basvuru {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "is_ilani_id", nullable = false)
    private IsIlani isIlani;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "is_arayan_id", nullable = false)
    private IsArayan isArayan;

    @Enumerated(EnumType.STRING)
    @Column(name = "basvuru_durumu", nullable = false)
    private BasvuruDurumu basvuruDurumu = BasvuruDurumu.BEKLIYOR;

    @Column(name = "basvuru_tarihi", updatable = false)
    private LocalDateTime basvuruTarihi;

    @PrePersist
    protected void onCreate() {
        this.basvuruTarihi = LocalDateTime.now();
    }

    public enum BasvuruDurumu {
        BEKLIYOR,
        INCELENDI,
        MULAKATA_CAGRILDI,
        REDDEDILDI
    }
}