package com.osb.panel.repository;

import com.osb.panel.domain.Sanayici;
import com.osb.panel.domain.Sanayici.Durum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SanayiciRepository extends JpaRepository<Sanayici, Long> {

    List<Sanayici> findAllByOrderByCompanyNameAsc();

    long countByStatus(Durum status);

    @Query("SELECT SUM(s.plotSizeM2) FROM Sanayici s WHERE s.status = :status")
    Long sumPlotSizeByStatus(Durum status);
}
