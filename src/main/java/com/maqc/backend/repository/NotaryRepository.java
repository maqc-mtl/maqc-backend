package com.maqc.backend.repository;

import com.maqc.backend.model.Notary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotaryRepository extends JpaRepository<Notary, Long> {
}
