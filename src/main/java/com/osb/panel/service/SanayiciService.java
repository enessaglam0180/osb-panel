package com.osb.panel.service;

import com.osb.panel.domain.Sanayici;
import com.osb.panel.domain.Sanayici.Durum;
import com.osb.panel.repository.SanayiciRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class SanayiciService {

    private final SanayiciRepository repository;

    public SanayiciService(SanayiciRepository repository) {
        this.repository = repository;
    }

    public List<Sanayici> findAll() {
        return repository.findAllByOrderByCompanyNameAsc();
    }

    public Optional<Sanayici> findById(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public Sanayici save(Sanayici sanayici) {
        return repository.save(sanayici);
    }

    @Transactional
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public long countByStatus(Durum status) {
        return repository.countByStatus(status);
    }

    public long countAll() {
        return repository.count();
    }

    public Long sumPlotSizeByStatus(Durum status) {
        Long result = repository.sumPlotSizeByStatus(status);
        return result != null ? result : 0L;
    }
}
