package com.osb.panel.web;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import java.io.File;
import java.io.FileInputStream;
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

@Named
@ViewScoped
public class BasvuruBean implements Serializable {

    @Autowired
    private transient IsIlaniRepository isIlaniRepository;
    @Autowired
    private transient IsArayanRepository isArayanRepository;
    @Autowired
    private transient BasvuruRepository basvuruRepository;
    @Autowired
    private transient FileStorageService fileStorageService;


    @Getter private List<IsIlani> aktifIlanlar;
    @Getter private List<Basvuru> tumBasvurular;


    @Getter @Setter private Long secilenIlanId;
    @Getter @Setter private String adSoyad;
    @Getter @Setter private String eposta;
    @Getter @Setter private String telefon;
    @Getter @Setter private String meslek;
    @Getter @Setter private UploadedFile cvDosyasi; // PrimeFaces dosya yükleme nesnesi

    @PostConstruct
    public void init() {
        yukle();
    }

    public void yukle() {
        // Formdaki dropdown (açılır liste) için sadece aktif ilanları getir
        aktifIlanlar = isIlaniRepository.findAll();
        // İK Paneli için tüm başvuruları getir
        tumBasvurular = basvuruRepository.findAll();
    }


    public void basvuruYap() {
        try {

            if (secilenIlanId == null || cvDosyasi == null || cvDosyasi.getSize() == 0) {
                addMessage(FacesMessage.SEVERITY_WARN, "Lütfen bir ilan seçin ve CV'nizi yükleyin.");
                return;
            }


            String dosyaYolu = fileStorageService.cvKaydet(cvDosyasi.getFileName(), cvDosyasi.getInputStream());


            IsArayan aday = new IsArayan();
            aday.setAdSoyad(adSoyad);
            aday.setEposta(eposta);
            aday.setTelefon(telefon);
            aday.setMeslek(meslek);
            aday.setCvDosyaYolu(dosyaYolu);
            isArayanRepository.save(aday);

            // 4. Hangi ilana başvurduğunu bul
            IsIlani ilan = isIlaniRepository.findById(secilenIlanId)
                    .orElseThrow(() -> new RuntimeException("İlan bulunamadı!"));

            // 5. Başvuruyu oluştur ve kaydet
            Basvuru basvuru = new Basvuru();
            basvuru.setIsIlani(ilan);
            basvuru.setIsArayan(aday);
            basvuru.setBasvuruDurumu(Basvuru.BasvuruDurumu.BEKLIYOR);
            basvuruRepository.save(basvuru);

            // 6. Formu temizle ve başarılı mesajı ver
            formuTemizle();
            yukle(); // Listeleri güncelle
            addMessage(FacesMessage.SEVERITY_INFO, "Başvurunuz başarıyla alındı!");

        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Başvuru sırasında hata oluştu: " + e.getMessage());
        }
    }

    // İK Yöneticisi başvuru durumunu değiştirdiğinde çalışır
    public void durumGuncelle(Basvuru basvuru) {
        basvuruRepository.save(basvuru);
        addMessage(FacesMessage.SEVERITY_INFO, "Adayın başvuru durumu güncellendi.");
    }

    private void formuTemizle() {
        this.secilenIlanId = null;
        this.adSoyad = null;
        this.eposta = null;
        this.telefon = null;
        this.meslek = null;
        this.cvDosyasi = null;
    }

    private void addMessage(FacesMessage.Severity severity, String summary) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, null));
    }

    // CV İndirme Metodu
    public StreamedContent cvIndir(Basvuru basvuru) {
        try {

            String dbYolu = basvuru.getIsArayan().getCvDosyaYolu();
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
}