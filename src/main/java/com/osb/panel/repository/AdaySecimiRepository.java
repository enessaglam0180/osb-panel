package com.osb.panel.repository;

import com.osb.panel.domain.AdaySecimi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdaySecimiRepository extends JpaRepository<AdaySecimi, Long> {

    List<AdaySecimi> findByIsverenId(Long isverenId);

    boolean existsByIsverenIdAndIsArayanId(Long isverenId, Long isArayanId);

    void deleteByIsverenIdAndIsArayanId(Long isverenId, Long isArayanId);

    void deleteByIsArayanId(Long isArayanId);
}
