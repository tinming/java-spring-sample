package com.sample.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sample.model.Vendor;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, Long> {
  List<Vendor> findByContactId(String contactId);
}