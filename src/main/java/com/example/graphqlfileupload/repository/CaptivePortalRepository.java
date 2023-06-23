package com.example.graphqlfileupload.repository;

import com.example.graphqlfileupload.model.CaptivePortal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CaptivePortalRepository extends JpaRepository<CaptivePortal, Long> {

}
