package com.osb.panel.web;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import java.io.File;
import java.io.FileInputStream;
import com.osb.panel.domain.Basvuru;
import com.osb.panel.domain.IsArayan;
import com.osb.panel.domain.IsIlani;
import com.osb.panel.repository.BasvuruRepository;
import com.osb.panel.repository.IsIlaniRepository;
import com.osb.panel.service.IsArayanService;
import com.osb.panel.service.AdaySecimiService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.List;

/**
 * Operatör İK Paneli bean'i.
 * Tam yetki: tüm başvuruları görüntüleme, durum güncelleme, aday silme,
 * istatistikleri görme, CV indirme.
 */
@Named
@ViewScoped
public class BasvuruBean implements Serializable {

    @Autowired
    private transient IsIlaniRepository isIlaniRepository;
    @Autowired
    private transient IsArayanService isArayanService;
    @Autowired
    private transient AdaySecimiService adaySecimiService;
    @Autowired
    private transient BasvuruRepository basvuruRepository;
    @Autowired
    private transient com.osb.panel.service.FileStorageService fileStorageService;

    @Getter private List<IsIlani> aktifIlanlar;
    @Getter private List<Basvuru> tumBasvurular;
    @Getter private List<IsArayan> tumAdaylar;

    // İstatistikler
    @Getter private long bekleyenSayisi;
    @Getter private long incelenenSayisi;
    @Getter private long mulakatSayisi;
    @Getter private long redSayisi;
    @Getter private long toplamAdaySayisi;

    // Filtreleme
    @Getter @Setter private String aramaMeslek;
    @Getter @Setter private String aramaSehir;

    @PostConstruct
    public void init() {
        yukle();
    }

    public void yukle() {
        aktifIlanlar = isIlaniRepository.findAll();
        tumBasvurular = basvuruRepository.findAll();
        tumAdaylar = isArayanService.findAll();
        istatistikleriGuncelle();
    }

    private void istatistikleriGuncelle() {
        bekleyenSayisi = basvuruRepository.countByBasvuruDurumu(Basvuru.BasvuruDurumu.BEKLIYOR);
        incelenenSayisi = basvuruRepository.countByBasvuruDurumu(Basvuru.BasvuruDurumu.INCELENDI);
        mulakatSayisi = basvuruRepository.countByBasvuruDurumu(Basvuru.BasvuruDurumu.MULAKATA_CAGRILDI);
        redSayisi = basvuruRepository.countByBasvuruDurumu(Basvuru.BasvuruDurumu.REDDEDILDI);
        toplamAdaySayisi = tumAdaylar != null ? tumAdaylar.size() : 0;
    }

    public void adayAra() {
        tumAdaylar = isArayanService.ara(aramaMeslek, aramaSehir);
    }

    public void filtreTemizle() {
        aramaMeslek = null;
        aramaSehir = null;
        tumAdaylar = isArayanService.findAll();
    }

    // İK Yöneticisi başvuru durumunu değiştirdiğinde çalışır
    public void durumGuncelle(Basvuru basvuru) {
        basvuruRepository.save(basvuru);
        
        
        IsArayan aday = basvuru.getIsArayan();
        String ilanBaslik = basvuru.getIsIlani() != null ? basvuru.getIsIlani().getBaslik() : "Genel Başvuru";
        
        if (basvuru.getBasvuruDurumu() == Basvuru.BasvuruDurumu.MULAKATA_CAGRILDI) {
            aday.setMulakataCagrildiMi(true);
            aday.setDurum("Mülakata Çağrıldı (" + ilanBaslik + ")");
        } else if (basvuru.getBasvuruDurumu() == Basvuru.BasvuruDurumu.REDDEDILDI) {
            aday.setDurum("Reddedildi (" + ilanBaslik + ")");
        } else if (basvuru.getBasvuruDurumu() == Basvuru.BasvuruDurumu.INCELENDI) {
            aday.setDurum("İncelendi (" + ilanBaslik + ")");
        } else {
            aday.setDurum("Bekliyor (" + ilanBaslik + ")");
        }
        isArayanService.save(aday);
        
        istatistikleriGuncelle();
        addMessage(FacesMessage.SEVERITY_INFO, "Başvuru durumu güncellendi ve Aday Havuzu ile senkronize edildi.");
    }

    // Aday bilgilerini güncellediğinde çalışır
    public void adayGuncelle(IsArayan aday) {
        isArayanService.save(aday);
        
        // Senkronizasyon: Aday mülakata çağrıldıysa, beklemedeki başvurularını da Mülakata Çağrıldı yap
        if (Boolean.TRUE.equals(aday.getMulakataCagrildiMi())) {
            List<Basvuru> basvurular = basvuruRepository.findByIsArayanId(aday.getId());
            boolean degisiklik = false;
            for (Basvuru b : basvurular) {
                if (b.getBasvuruDurumu() == Basvuru.BasvuruDurumu.BEKLIYOR || b.getBasvuruDurumu() == Basvuru.BasvuruDurumu.INCELENDI) {
                    b.setBasvuruDurumu(Basvuru.BasvuruDurumu.MULAKATA_CAGRILDI);
                    basvuruRepository.save(b);
                    degisiklik = true;
                }
            }
            if (degisiklik) {
                yukle(); // Tüm listeyi güncellemek için
            }
        }
        
        addMessage(FacesMessage.SEVERITY_INFO, "Aday bilgileri güncellendi ve başvurularla senkronize edildi.");
    }

    // Operatör adayı siler
    public void adaySil(IsArayan aday) {
        try {
            // Önce adayın başvurularını sil
            List<Basvuru> adayBasvurulari = basvuruRepository.findByIsArayanId(aday.getId());
            basvuruRepository.deleteAll(adayBasvurulari);

            // İşverenlerin bu adayı seçtikleri kayıtları (AdaySecimi) sil
            adaySecimiService.deleteByIsArayanId(aday.getId());

            // Sonra adayı sil
            isArayanService.deleteById(aday.getId());
            
            // Fiziksel CV'sini sil
            if (aday.getCvDosyaYolu() != null) {
                fileStorageService.cvSil(aday.getCvDosyaYolu());
            }

            yukle();
            addMessage(FacesMessage.SEVERITY_WARN, aday.getAdSoyad() + " adayı silindi.");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Silme hatası: " + e.getMessage());
        }
    }

    // Operatör başvuruyu siler
    public void basvuruSil(Basvuru basvuru) {
        try {
            basvuruRepository.delete(basvuru);
            yukle();
            addMessage(FacesMessage.SEVERITY_WARN, "Başvuru silindi.");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Silme hatası: " + e.getMessage());
        }
    }

    // CV İndirme Metodu
    public StreamedContent cvIndir(Basvuru basvuru) {
        try {
            String dbYolu = basvuru.getIsArayan().getCvDosyaYolu();
            if (dbYolu == null || dbYolu.isEmpty()) {
                addMessage(FacesMessage.SEVERITY_WARN, "CV dosyası bulunmuyor.");
                return null;
            }

            String dosyaAdi = dbYolu.substring(dbYolu.lastIndexOf("/") + 1);
            File dosya = new File("uploads/cv/" + dosyaAdi);

            if (!dosya.exists()) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Fiziksel dosya sunucuda bulunamadı.");
                return null;
            }

            String indirmeAdi = basvuru.getIsArayan().getAdSoyad().replace(" ", "_") + "_CV.pdf";

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

    // Aday listesinden doğrudan CV indirme
    public StreamedContent adayCvIndir(IsArayan aday) {
        try {
            String dbYolu = aday.getCvDosyaYolu();
            if (dbYolu == null || dbYolu.isEmpty()) {
                addMessage(FacesMessage.SEVERITY_WARN, "CV dosyası bulunmuyor.");
                return null;
            }

            String dosyaAdi = dbYolu.substring(dbYolu.lastIndexOf("/") + 1);
            File dosya = new File("uploads/cv/" + dosyaAdi);

            if (!dosya.exists()) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Fiziksel dosya sunucuda bulunamadı.");
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