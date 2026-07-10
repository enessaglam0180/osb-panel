package com.osb.panel.repository;

import com.osb.panel.domain.IsIlani;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IsIlaniRepository extends JpaRepository<IsIlani, Long> {
}