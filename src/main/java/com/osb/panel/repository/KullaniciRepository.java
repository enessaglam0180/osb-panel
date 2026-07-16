package com.osb.panel.repository;

import com.osb.panel.domain.Kullanici;
import com.osb.panel.domain.Kullanici.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KullaniciRepository extends JpaRepository<Kullanici, Long> {

    Optional<Kullanici> findByKullaniciAdi(String kullaniciAdi);

    List<Kullanici> findByRol(Rol rol);

    boolean existsByKullaniciAdi(String kullaniciAdi);
}
