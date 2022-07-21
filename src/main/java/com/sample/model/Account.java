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
 * Xero Account
 * See https://developer.xero.com/documentation/api/accounts
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "accounts")
public class Account {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private String code;
  private String name;

  @Column(unique = true)
  private String accountId; // unique xero id
  private String type;
  private String status;
  @Column(length=1024)
  private String description;
  private boolean enablePaymentsToAccount;

  @CreatedDate
  @Column(nullable = false, updatable = false)
  private LocalDateTime createdDate;

  @LastModifiedDate
  @Column(nullable = false)
  private LocalDateTime updatedAt;

  public Account() {
  }

  public Account(Long id, String code, String name, String accountId, String type, String status, String description, boolean enablePaymentsToAccount, LocalDateTime createdDate, LocalDateTime updatedAt) {
    this.id = id;
    this.code = code;
    this.name = name;
    this.accountId = accountId;
    this.type = type;
    this.status = status;
    this.description = description;
    this.enablePaymentsToAccount = enablePaymentsToAccount;
    this.createdDate = createdDate;
    this.updatedAt = updatedAt;
  }

  public Long getId() {
    return id;
  }

  public String getCode() {
    return code;
  }

  public String getName() {
    return name;
  }

  public String getAccountId() {
    return accountId;
  }

  public String getType() {
    return type;
  }

  public String getStatus() {
    return status;
  }

  public String getDescription() {
    return description;
  }

  public boolean getEnablePaymentsToAccount() {
    return enablePaymentsToAccount;
  }

  public LocalDateTime getCreatedDate() {
    return createdDate;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setAccountId(String accountId) {
    this.accountId = accountId;
  }

  public void setType(String type) {
    this.type = type;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setEnablePaymentsToAccount(boolean enablePaymentsToAccount) {
    this.enablePaymentsToAccount = enablePaymentsToAccount;
  }

  public void setCreatedDate(LocalDateTime createdDate) {
    this.createdDate = createdDate;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }
}