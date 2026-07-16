package com.osb.panel.service;

import com.osb.panel.domain.IsArayan;
import com.osb.panel.repository.IsArayanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class IsArayanService {

    private final IsArayanRepository repository;

    public IsArayanService(IsArayanRepository repository) {
        this.repository = repository;
    }

    public List<IsArayan> findAll() {
        return repository.findAll();
    }

    public Optional<IsArayan> findById(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public IsArayan save(IsArayan isArayan) {
        return repository.save(isArayan);
    }

    @Transactional
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public List<IsArayan> ara(String meslek, String sehir) {
        String meslekParam = (meslek != null && !meslek.trim().isEmpty()) ? meslek.trim() : null;
        String sehirParam = (sehir != null && !sehir.trim().isEmpty()) ? sehir.trim() : null;

        if (meslekParam == null && sehirParam == null) {
            return findAll();
        }
        return repository.aramaYap(meslekParam, sehirParam);
    }

    public boolean epostaKullanimda(String eposta) {
        return repository.existsByEposta(eposta);
    }
}
