package com.osb.panel.service;

import com.osb.panel.domain.Kullanici;
import com.osb.panel.domain.Kullanici.Rol;
import com.osb.panel.repository.KullaniciRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class KullaniciService implements UserDetailsService {

    private final KullaniciRepository repository;
    private final PasswordEncoder passwordEncoder;

    public KullaniciService(KullaniciRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Kullanici kullanici = repository.findByKullaniciAdi(username)
                .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı: " + username));

        if (!kullanici.isAktif()) {
            throw new UsernameNotFoundException("Kullanıcı hesabı devre dışı: " + username);
        }

        String roleName = "ROLE_" + kullanici.getRol().name();
        return new User(
                kullanici.getKullaniciAdi(),
                kullanici.getSifre(),
                Collections.singletonList(new SimpleGrantedAuthority(roleName))
        );
    }

    public Optional<Kullanici> findByKullaniciAdi(String kullaniciAdi) {
        return repository.findByKullaniciAdi(kullaniciAdi);
    }

    public List<Kullanici> findAll() {
        return repository.findAll();
    }

    public List<Kullanici> findByRol(Rol rol) {
        return repository.findByRol(rol);
    }

    public Optional<Kullanici> findById(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public Kullanici save(Kullanici kullanici) {
        return repository.save(kullanici);
    }

    @Transactional
    public Kullanici kaydet(String kullaniciAdi, String sifre, String adSoyad, String eposta, Rol rol) {
        Kullanici k = new Kullanici();
        k.setKullaniciAdi(kullaniciAdi);
        k.setSifre(passwordEncoder.encode(sifre));
        k.setAdSoyad(adSoyad);
        k.setEposta(eposta);
        k.setRol(rol);
        return repository.save(k);
    }

    @Transactional
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public boolean existsByKullaniciAdi(String kullaniciAdi) {
        return repository.existsByKullaniciAdi(kullaniciAdi);
    }
}
