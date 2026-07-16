package com.osb.panel.web;

import com.osb.panel.domain.AdaySecimi;
import com.osb.panel.domain.IsArayan;
import com.osb.panel.domain.Kullanici;
import com.osb.panel.service.AdaySecimiService;
import com.osb.panel.service.IsArayanService;
import com.osb.panel.service.KullaniciService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.util.List;

/**
 * İşveren paneli bean'i.
 * İşverenler aday havuzunu görüntüler, filtreler ve beğendiği adayları seçer.
 * Düzenleme/silme/durum güncelleme yetkisi YOKTUR.
 */
@Named
@ViewScoped
public class IsverenBean implements Serializable {

    @Autowired
    private transient IsArayanService isArayanService;
    @Autowired
    private transient AdaySecimiService adaySecimiService;
    @Autowired
    private transient KullaniciService kullaniciService;

    @Getter private List<IsArayan> adayHavuzu;
    @Getter private List<AdaySecimi> secimlerim;
    @Getter private Kullanici girisYapanIsveren;

    // Filtreleme
    @Getter @Setter private String aramaMeslek;
    @Getter @Setter private String aramaSehir;

    // Seçim notu
    @Getter @Setter private String secimNotu;
    @Getter @Setter private IsArayan secilenAday;

    @PostConstruct
    public void init() {
        // Giriş yapan işvereni bul
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        girisYapanIsveren = kullaniciService.findByKullaniciAdi(username).orElse(null);

        yukle();
    }

    public void yukle() {
        adayHavuzu = isArayanService.findAll();
        if (girisYapanIsveren != null) {
            secimlerim = adaySecimiService.findByIsveren(girisYapanIsveren.getId());
        }
    }

    public void ara() {
        adayHavuzu = isArayanService.ara(aramaMeslek, aramaSehir);
    }

    public void filtreTemizle() {
        aramaMeslek = null;
        aramaSehir = null;
        yukle();
    }

    public void adaySecmeHazirla(IsArayan aday) {
        this.secilenAday = aday;
        this.secimNotu = null;
    }

    public void adaySec() {
        try {
            if (girisYapanIsveren == null || secilenAday == null) {
                addMessage(FacesMessage.SEVERITY_WARN, "İşlem yapılamadı.");
                return;
            }

            adaySecimiService.sec(girisYapanIsveren, secilenAday, secimNotu);
            yukle();
            addMessage(FacesMessage.SEVERITY_INFO,
                    secilenAday.getAdSoyad() + " başarıyla seçildi.");
            secilenAday = null;
            secimNotu = null;
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_WARN, e.getMessage());
        }
    }

    public void secimKaldir(AdaySecimi secim) {
        try {
            adaySecimiService.deleteById(secim.getId());
            yukle();
            addMessage(FacesMessage.SEVERITY_INFO, "Seçim kaldırıldı.");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Hata: " + e.getMessage());
        }
    }

    public boolean adayZatenSecilmisMi(IsArayan aday) {
        if (girisYapanIsveren == null) return false;
        return adaySecimiService.zadenSecilmisMi(girisYapanIsveren.getId(), aday.getId());
    }

    // CV İndirme
    public StreamedContent cvIndir(IsArayan aday) {
        try {
            String dbYolu = aday.getCvDosyaYolu();
            if (dbYolu == null || dbYolu.isEmpty()) {
                addMessage(FacesMessage.SEVERITY_WARN, "Bu adayın CV dosyası bulunmuyor.");
                return null;
            }

            String dosyaAdi = dbYolu.substring(dbYolu.lastIndexOf("/") + 1);
            File dosya = new File("uploads/cv/" + dosyaAdi);

            if (!dosya.exists()) {
                addMessage(FacesMessage.SEVERITY_ERROR, "CV dosyası sunucuda bulunamadı.");
                return null;
            }

            String indirmeAdi = aday.getAdSoyad().replace(" ", "_") + "_CV.pdf";

            return DefaultStreamedContent.builder()
                    .name(indirmeAdi)
                    .contentType("application/pdf")
                    .stream(() -> {
                        try {
                            return new FileInputStream(dosya);
                        } catch (Exception e) {
                            return null;
                        }
                    })
                    .build();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "İndirme hatası: " + e.getMessage());
            return null;
        }
    }

    private void addMessage(FacesMessage.Severity severity, String summary) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, null));
    }
}
