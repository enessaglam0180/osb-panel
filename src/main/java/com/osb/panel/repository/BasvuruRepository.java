package com.osb.panel.repository;

import com.osb.panel.domain.Basvuru;
import com.osb.panel.domain.Basvuru.BasvuruDurumu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BasvuruRepository extends JpaRepository<Basvuru, Long> {

    List<Basvuru> findByIsArayanId(Long isArayanId);

    long countByBasvuruDurumu(BasvuruDurumu durumu);
}