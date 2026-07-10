package com.osb.panel.repository;

import com.osb.panel.domain.Basvuru;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BasvuruRepository extends JpaRepository<Basvuru, Long> {
}