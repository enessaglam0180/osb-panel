package com.osb.panel.web;

import com.osb.panel.domain.Basvuru;
import com.osb.panel.domain.IsArayan;
import com.osb.panel.domain.IsIlani;
import com.osb.panel.repository.BasvuruRepository;
import com.osb.panel.repository.IsArayanRepository;
import com.osb.panel.repository.IsIlaniRepository;
import com.osb.panel.service.FileStorageService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.model.file.UploadedFile;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.List;

/**
 * Public aday başvuru formu bean'i.
 * Login gerektirmeden /aday-basvuru.xhtml sayfasında kullanılır.
 */
@Named
@ViewScoped
public class AdayBasvuruBean implements Serializable {

    @Autowired
    private transient IsIlaniRepository isIlaniRepository;
    @Autowired
    private transient IsArayanRepository isArayanRepository;
    @Autowired
    private transient BasvuruRepository basvuruRepository;
    @Autowired
    private transient FileStorageService fileStorageService;

    @Getter private List<IsIlani> aktifIlanlar;

    // Form alanları
    @Getter @Setter private Long secilenIlanId;
    @Getter @Setter private String adSoyad;
    @Getter @Setter private String eposta;
    @Getter @Setter private String telefon;
    @Getter @Setter private String meslek;
    @Getter @Setter private Integer deneyimYili;
    @Getter @Setter private String egitimDurumu;
    @Getter @Setter private String sehir;
    @Getter @Setter private UploadedFile cvDosyasi;

    @Getter private boolean basvuruBasarili = false;

    @PostConstruct
    public void init() {
        aktifIlanlar = isIlaniRepository.findAll();
    }

    public void basvuruYap() {
        try {
            if (cvDosyasi == null || cvDosyasi.getSize() == 0) {
                addMessage(FacesMessage.SEVERITY_WARN, "Lütfen CV'nizi yükleyin.");
                return;
            }

            if (adSoyad == null || adSoyad.trim().isEmpty()) {
                addMessage(FacesMessage.SEVERITY_WARN, "Ad Soyad zorunludur.");
                return;
            }

            if (eposta == null || eposta.trim().isEmpty()) {
                addMessage(FacesMessage.SEVERITY_WARN, "E-posta zorunludur.");
                return;
            }

            // Aynı e-posta ile kayıtlı aday var mı kontrol et
            IsArayan aday = isArayanRepository.findByEposta(eposta.trim()).orElse(null);

            // Mükerrer başvuru kontrolü
            if (aday != null) {
                boolean zatenBasvurdu = false;
                if (secilenIlanId != null) {
                    zatenBasvurdu = basvuruRepository.existsByIsArayanIdAndIsIlaniId(aday.getId(), secilenIlanId);
                } else {
                    zatenBasvurdu = basvuruRepository.existsByIsArayanIdAndIsIlaniIsNull(aday.getId());
                }
                
                if (zatenBasvurdu) {
                    addMessage(FacesMessage.SEVERITY_WARN, "Bu ilana (veya genel havuza) daha önce başvuru yaptınız.");
                    return;
                }
            }

            // CV'yi kaydet
            String dosyaYolu = fileStorageService.cvKaydet(cvDosyasi.getFileName(), cvDosyasi.getInputStream());

            if (aday != null) {
                // Eski CV'yi diskten sil
                if (aday.getCvDosyaYolu() != null) {
                    fileStorageService.cvSil(aday.getCvDosyaYolu());
                }

                // Mevcut adayın bilgilerini güncelle ve yeni CV'sini kaydet
                aday.setAdSoyad(adSoyad.trim());
                aday.setTelefon(telefon);
                aday.setMeslek(meslek);
                aday.setDeneyimYili(deneyimYili);
                aday.setEgitimDurumu(egitimDurumu);
                aday.setSehir(sehir);
                aday.setCvDosyaYolu(dosyaYolu);
            } else {
                // Yeni aday oluştur
                aday = new IsArayan();
                aday.setAdSoyad(adSoyad.trim());
                aday.setEposta(eposta.trim());
                aday.setTelefon(telefon);
                aday.setMeslek(meslek);
                aday.setDeneyimYili(deneyimYili);
                aday.setEgitimDurumu(egitimDurumu);
                aday.setSehir(sehir);
                aday.setCvDosyaYolu(dosyaYolu);
            }
            isArayanRepository.save(aday);

            // Başvuruyu oluştur
            Basvuru basvuru = new Basvuru();
            basvuru.setIsArayan(aday);
            basvuru.setBasvuruDurumu(Basvuru.BasvuruDurumu.BEKLIYOR);

            // İlan seçildiyse bağla
            if (secilenIlanId != null) {
                IsIlani ilan = isIlaniRepository.findById(secilenIlanId)
                        .orElse(null);
                basvuru.setIsIlani(ilan);
            }

            basvuruRepository.save(basvuru);

            // Başarılı
            basvuruBasarili = true;
            formuTemizle();
            addMessage(FacesMessage.SEVERITY_INFO, "Başvurunuz başarıyla alındı! En kısa sürede sizinle iletişime geçilecektir.");

        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Başvuru sırasında hata oluştu: " + e.getMessage());
        }
    }

    private void formuTemizle() {
        this.secilenIlanId = null;
        this.adSoyad = null;
        this.eposta = null;
        this.telefon = null;
        this.meslek = null;
        this.deneyimYili = null;
        this.egitimDurumu = null;
        this.sehir = null;
        this.cvDosyasi = null;
    }

    private void addMessage(FacesMessage.Severity severity, String summary) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, null));
    }
}
