package com.osb.panel.repository;

import com.osb.panel.domain.IsArayan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IsArayanRepository extends JpaRepository<IsArayan, Long> {
}