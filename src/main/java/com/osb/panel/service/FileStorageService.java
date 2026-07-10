package com.osb.panel.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path rootLocation = Paths.get("uploads/cv");


    public FileStorageService() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("CV klasörü oluşturulamadı!", e);
        }
    }

    public String cvKaydet(String orjinalDosyaAdi, InputStream dosyaIcerigi) {
        try {

            String temizDosyaAdi = StringUtils.cleanPath(orjinalDosyaAdi);


            String uzanti = "";
            if (temizDosyaAdi.contains(".")) {
                uzanti = temizDosyaAdi.substring(temizDosyaAdi.lastIndexOf("."));
            }

            String yeniDosyaAdi = UUID.randomUUID().toString() + uzanti;

            Path hedefYer = this.rootLocation.resolve(yeniDosyaAdi);
            Files.copy(dosyaIcerigi, hedefYer, StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/cv/" + yeniDosyaAdi;

        } catch (IOException e) {
            throw new RuntimeException("Dosya diske yazılamadı: " + e.getMessage());
        }
    }
}