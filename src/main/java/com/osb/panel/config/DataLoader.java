package com.osb.panel.config;

import com.osb.panel.domain.*;
import com.osb.panel.domain.Kullanici.Rol;
import com.osb.panel.domain.Sanayici.Durum;
import com.osb.panel.repository.*;
import com.osb.panel.service.KullaniciService;
import com.osb.panel.service.SanayiciService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final SanayiciService service;
    private final IsIlaniRepository isIlaniRepository;
    private final IsArayanRepository isArayanRepository;
    private final BasvuruRepository basvuruRepository;
    private final KullaniciService kullaniciService;

    public DataLoader(SanayiciService service,
                      IsIlaniRepository isIlaniRepository,
                      IsArayanRepository isArayanRepository,
                      BasvuruRepository basvuruRepository,
                      KullaniciService kullaniciService) {
        this.service = service;
        this.isIlaniRepository = isIlaniRepository;
        this.isArayanRepository = isArayanRepository;
        this.basvuruRepository = basvuruRepository;
        this.kullaniciService = kullaniciService;
    }

    @Override
    public void run(String... args) {

        // --- 1. KULLANICI VERİLERİ KONTROLÜ ---
        if (!kullaniciService.existsByKullaniciAdi("operator")) {
            kullaniciService.kaydet("operator", "operator123", "Sistem Operatörü", "operator@osb.com", Rol.OPERATOR);
            System.out.println("Operatör kullanıcısı oluşturuldu. (operator / operator123)");
        }

        if (!kullaniciService.existsByKullaniciAdi("isveren1")) {
            kullaniciService.kaydet("isveren1", "isveren123", "Yıldız Tekstil İK", "ik@yildiz.com", Rol.ISVEREN);
            System.out.println("İşveren kullanıcısı oluşturuldu. (isveren1 / isveren123)");
        }

        if (!kullaniciService.existsByKullaniciAdi("isveren2")) {
            kullaniciService.kaydet("isveren2", "isveren123", "Güneş Gıda İK", "ik@gunes.com", Rol.ISVEREN);
            System.out.println("İşveren kullanıcısı oluşturuldu. (isveren2 / isveren123)");
        }

        // --- 2. SANAYİCİ VERİLERİ KONTROLÜ ---
        if (service.countAll() == 0) {
            service.save(build("Yıldız Tekstil A.Ş.",      "1234567890", "Ali Yıldız",     "0532 111 2233", "ali@yildiz.com",     "Textile",   Durum.ACTIVE,    42500));
            service.save(build("Güneş Gıda Sanayi Ltd.",    "9876543210", "Fatma Demir",    "0533 222 3344", "fatma@gunes.com",    "Food",      Durum.ACTIVE,    18000));
            service.save(build("Demir Çelik Endüstri A.Ş.", "1122334455", "Mehmet Çelik",   "0534 333 4455", "mehmet@demir.com",   "Metal",     Durum.PASSIVE,   65000));
            service.save(build("Anadolu Kimya San. Tic.",   "5544332211", "Ayşe Kaya",      "0535 444 5566", "ayse@anadolu.com",   "Chemistry", Durum.ACTIVE,    31200));
            service.save(build("Plastik Dünya A.Ş.",        "6677889900", "Hasan Çam",      "0536 555 6677", "hasan@plastik.com",  "Plastic",   Durum.SUSPENDED, 27800));
            service.save(build("Mega Makine Sanayi",         "7788990011", "Zeynep Arslan",  "0537 666 7788", "zeynep@mega.com",    "Machinery", Durum.ACTIVE,    54000));
            service.save(build("Ege Elektronik Ltd.",        "8899001122", "Burak Öztürk",   "0538 777 8899", "burak@ege.com",      "Electronics",Durum.ACTIVE,   22500));
            service.save(build("Karadeniz Mobilya A.Ş.",    "9900112233", "Selin Yılmaz",   "0539 888 9900", "selin@karaden.com",  "Furniture", Durum.PASSIVE,   16800));
            service.save(build("Akdeniz Cam Sanayi",         "1010101010", "Murat Koç",      "0542 999 0011", "murat@akdeniz.com",  "Glass",     Durum.ACTIVE,    38400));
            service.save(build("İç Anadolu Lastik Ltd.",    "2020202020", "Deniz Şahin",    "0543 000 1122", "deniz@icalast.com",  "Rubber",    Durum.SUSPENDED, 29100));
            System.out.println("Sanayici tohum verileri eklendi.");
        } else {
            System.out.println("Veritabanı zaten dolu, Sanayici verileri eklenmeyecek...");
        }

        // --- 3. İK MODÜLÜ VERİLERİ KONTROLÜ ---
        if (isIlaniRepository.count() == 0) {
            List<Sanayici> firmalar = service.findAll();

            // Eğer sistemde hiç firma yoksa, boşluğa ilan açamayız, güvenlik kontrolü yapalım.
            if (!firmalar.isEmpty()) {
                Sanayici firma1 = firmalar.get(0);
                Sanayici firma2 = firmalar.size() > 1 ? firmalar.get(1) : firma1;

                IsIlani ilan1 = new IsIlani();
                ilan1.setSanayici(firma1);
                ilan1.setBaslik("Java Spring Boot Geliştirici");
                ilan1.setAciklama("AOSB sistemlerimiz için tam zamanlı backend geliştirici aranmaktadır.");
                isIlaniRepository.save(ilan1);

                IsIlani ilan2 = new IsIlani();
                ilan2.setSanayici(firma2);
                ilan2.setBaslik("CNC Operatörü");
                ilan2.setAciklama("Üretim hattı için deneyimli CNC operatörü aranmaktadır. En az 3 yıl tecrübe.");
                isIlaniRepository.save(ilan2);

                IsIlani ilan3 = new IsIlani();
                ilan3.setSanayici(firma1);
                ilan3.setBaslik("Kalite Kontrol Mühendisi");
                ilan3.setAciklama("ISO 9001 standartlarına hakim kalite kontrol mühendisi aranmaktadır.");
                isIlaniRepository.save(ilan3);

                IsArayan aday1 = new IsArayan();
                aday1.setAdSoyad("Mahmut Enes");
                aday1.setEposta("enes@example.com");
                aday1.setTelefon("0555 999 8877");
                aday1.setMeslek("Bilgisayar Mühendisi");
                aday1.setDeneyimYili(5);
                aday1.setEgitimDurumu("Lisans");
                aday1.setSehir("Adana");
                aday1.setCvDosyaYolu("/uploads/cv/mahmut_enes_cv.pdf");
                isArayanRepository.save(aday1);

                IsArayan aday2 = new IsArayan();
                aday2.setAdSoyad("Ayşe Yılmaz");
                aday2.setEposta("ayse.yilmaz@example.com");
                aday2.setTelefon("0544 123 4567");
                aday2.setMeslek("CNC Operatörü");
                aday2.setDeneyimYili(8);
                aday2.setEgitimDurumu("Meslek Yüksekokulu");
                aday2.setSehir("Mersin");
                aday2.setCvDosyaYolu("/uploads/cv/ayse_yilmaz_cv.pdf");
                isArayanRepository.save(aday2);

                IsArayan aday3 = new IsArayan();
                aday3.setAdSoyad("Mehmet Kara");
                aday3.setEposta("mehmet.kara@example.com");
                aday3.setTelefon("0533 987 6543");
                aday3.setMeslek("Kalite Kontrol Mühendisi");
                aday3.setDeneyimYili(3);
                aday3.setEgitimDurumu("Lisans");
                aday3.setSehir("Adana");
                aday3.setCvDosyaYolu("/uploads/cv/mehmet_kara_cv.pdf");
                isArayanRepository.save(aday3);

                Basvuru basvuru1 = new Basvuru();
                basvuru1.setIsIlani(ilan1);
                basvuru1.setIsArayan(aday1);
                basvuru1.setBasvuruDurumu(Basvuru.BasvuruDurumu.BEKLIYOR);
                basvuruRepository.save(basvuru1);

                Basvuru basvuru2 = new Basvuru();
                basvuru2.setIsIlani(ilan2);
                basvuru2.setIsArayan(aday2);
                basvuru2.setBasvuruDurumu(Basvuru.BasvuruDurumu.INCELENDI);
                basvuruRepository.save(basvuru2);

                Basvuru basvuru3 = new Basvuru();
                basvuru3.setIsIlani(ilan3);
                basvuru3.setIsArayan(aday3);
                basvuru3.setBasvuruDurumu(Basvuru.BasvuruDurumu.BEKLIYOR);
                basvuruRepository.save(basvuru3);

                System.out.println("İK Modülü tohum verileri eklendi.");
            }
        } else {
            System.out.println("İK Modülü verileri zaten mevcut, geçiliyor...");
        }
    }

    private Sanayici build(String name, String taxNo, String contact,
                           String phone, String email, String sector,
                           Durum status, int plotSize) {
        Sanayici s = new Sanayici();
        s.setCompanyName(name);
        s.setTaxNumber(taxNo);
        s.setContactPerson(contact);
        s.setPhone(phone);
        s.setEmail(email);
        s.setSector(sector);
        s.setStatus(status);
        s.setPlotSizeM2(plotSize);
        return s;
    }
}