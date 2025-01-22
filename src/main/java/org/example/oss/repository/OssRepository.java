package org.example.oss.repository;

import org.example.oss.model.Oss;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OssRepository extends JpaRepository<Oss, Long> {
}