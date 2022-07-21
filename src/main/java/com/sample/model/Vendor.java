package com.sample.model;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Xero Contact
 * See https://developer.xero.com/documentation/api/contacts
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "vendors")
public class Vendor {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(unique = true)
  private String contactId; // unique xero id
  private String contactNumber;
  private String name;

  @CreatedDate
  @Column(nullable = false, updatable = false)
  private LocalDateTime createdDate;

  @LastModifiedDate
  @Column(nullable = false)
  private LocalDateTime updatedAt;

  public Vendor() {
  }

  public Vendor(Long id, String contactId, String contactNumber, String name) {
    this.id = id;
    this.contactId = contactId;
    this.contactNumber = contactNumber;
    this.name = name;
  }

  public Long getId() {
    return id;
  }

  public String getContactId() {
    return contactId;
  }

  public String getContactNumber() {
    return contactNumber;
  }

  public String getName() {
    return name;
  }

  public LocalDateTime getCreatedDate() {
    return createdDate;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setContactId(String contactId) {
    this.contactId = contactId;
  }

  public void setContactNumber(String contactNumber) {
    this.contactNumber = contactNumber;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setCreatedDate(LocalDateTime createdDate) {
    this.createdDate = createdDate;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }
}