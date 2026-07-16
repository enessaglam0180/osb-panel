package com.osb.panel.repository;

import com.osb.panel.domain.IsArayan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IsArayanRepository extends JpaRepository<IsArayan, Long> {

    List<IsArayan> findByMeslekContainingIgnoreCase(String meslek);

    List<IsArayan> findBySehirContainingIgnoreCase(String sehir);

    @Query("SELECT a FROM IsArayan a WHERE " +
            "(:meslek IS NULL OR LOWER(a.meslek) LIKE LOWER(CONCAT('%', :meslek, '%'))) AND " +
            "(:sehir IS NULL OR LOWER(a.sehir) LIKE LOWER(CONCAT('%', :sehir, '%')))")
    List<IsArayan> aramaYap(@Param("meslek") String meslek, @Param("sehir") String sehir);

    boolean existsByEposta(String eposta);
}