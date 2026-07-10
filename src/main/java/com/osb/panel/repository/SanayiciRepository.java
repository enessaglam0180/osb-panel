package com.osb.panel.repository;

import com.osb.panel.domain.Sanayici;
import com.osb.panel.domain.Sanayici.Durum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param; // Arama parametresi için eklendi

import java.util.List;

public interface SanayiciRepository extends JpaRepository<Sanayici, Long> {

    List<Sanayici> findAllByOrderByCompanyNameAsc();

    long countByStatus(Durum status);

    @Query("SELECT SUM(s.plotSizeM2) FROM Sanayici s WHERE s.status = :status")
    Long sumPlotSizeByStatus(Durum status);

    @Query("SELECT s FROM Sanayici s WHERE " +
            "LOWER(s.companyName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Sanayici> searchSanayici(@Param("keyword") String keyword);
}