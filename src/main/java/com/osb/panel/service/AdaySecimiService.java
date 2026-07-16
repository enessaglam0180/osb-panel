package com.osb.panel.service;

import com.osb.panel.domain.AdaySecimi;
import com.osb.panel.domain.IsArayan;
import com.osb.panel.domain.Kullanici;
import com.osb.panel.repository.AdaySecimiRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class AdaySecimiService {

    private final AdaySecimiRepository repository;

    public AdaySecimiService(AdaySecimiRepository repository) {
        this.repository = repository;
    }

    public List<AdaySecimi> findByIsveren(Long isverenId) {
        return repository.findByIsverenId(isverenId);
    }

    public boolean zadenSecilmisMi(Long isverenId, Long isArayanId) {
        return repository.existsByIsverenIdAndIsArayanId(isverenId, isArayanId);
    }

    @Transactional
    public AdaySecimi sec(Kullanici isveren, IsArayan isArayan, String notlar) {
        if (zadenSecilmisMi(isveren.getId(), isArayan.getId())) {
            throw new RuntimeException("Bu aday zaten seçilmiş!");
        }
        AdaySecimi secim = new AdaySecimi();
        secim.setIsveren(isveren);
        secim.setIsArayan(isArayan);
        secim.setNotlar(notlar);
        return repository.save(secim);
    }

    @Transactional
    public void secimKaldir(Long isverenId, Long isArayanId) {
        repository.deleteByIsverenIdAndIsArayanId(isverenId, isArayanId);
    }

    @Transactional
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Transactional
    public void deleteByIsArayanId(Long isArayanId) {
        repository.deleteByIsArayanId(isArayanId);
    }
}
