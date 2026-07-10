package com.osb.panel.config;

import com.osb.panel.domain.*;
import com.osb.panel.domain.Sanayici.Durum;
import com.osb.panel.service.SanayiciService;
import com.osb.panel.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final SanayiciService service;
    private final IsIlaniRepository isIlaniRepository;
    private final IsArayanRepository isArayanRepository;
    private final BasvuruRepository basvuruRepository;

    public DataLoader(SanayiciService service,
                      IsIlaniRepository isIlaniRepository,
                      IsArayanRepository isArayanRepository,
                      BasvuruRepository basvuruRepository) {
        this.service = service;
        this.isIlaniRepository = isIlaniRepository;
        this.isArayanRepository = isArayanRepository;
        this.basvuruRepository = basvuruRepository;
    }

    @Override
    public void run(String... args) {

        // --- 1. SANAYİCİ VERİLERİ KONTROLÜ ---
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

        // --- 2. İK MODÜLÜ VERİLERİ KONTROLÜ ---
        if (isIlaniRepository.count() == 0) {
            List<Sanayici> firmalar = service.findAll();

            // Eğer sistemde hiç firma yoksa, boşluğa ilan açamayız, güvenlik kontrolü yapalım.
            if (!firmalar.isEmpty()) {
                Sanayici firma = firmalar.get(0); // İlk firmayı al

                IsIlani ilan = new IsIlani();
                ilan.setSanayici(firma);
                ilan.setBaslik("Java Spring Boot Geliştirici");
                ilan.setAciklama("AOSB sistemlerimiz için tam zamanlı backend geliştirici aranmaktadır.");
                isIlaniRepository.save(ilan);

                IsArayan aday = new IsArayan();
                aday.setAdSoyad("Mahmut Enes");
                aday.setEposta("enes@example.com");
                aday.setTelefon("0555 999 8877");
                aday.setMeslek("Bilgisayar Mühendisi");
                aday.setCvDosyaYolu("/uploads/cv/mahmut_enes_cv.pdf");
                isArayanRepository.save(aday);

                Basvuru basvuru = new Basvuru();
                basvuru.setIsIlani(ilan);
                basvuru.setIsArayan(aday);
                basvuru.setBasvuruDurumu(Basvuru.BasvuruDurumu.BEKLIYOR);
                basvuruRepository.save(basvuru);

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