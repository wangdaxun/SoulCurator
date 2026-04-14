package com.soulcurator.backend.repository;

import com.soulcurator.backend.model.SimpleWork;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SimpleWorkRepository extends JpaRepository<SimpleWork, Long> {
}
