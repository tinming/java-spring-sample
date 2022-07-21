package com.sample.service.xero;

public class XeroToken {
  private String jwtToken;
  private String accessToken;
  private String refreshToken;
  private String expiresInSeconds;
  private String xeroTenantId;

  public XeroToken() {
  }

  public XeroToken(String jwtToken, String accessToken, String refreshToken, String expiresInSeconds, String xeroTenantId) {
    this.jwtToken = jwtToken;
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
    this.expiresInSeconds = expiresInSeconds;
    this.xeroTenantId = xeroTenantId;
  }

  public String getJwtToken() {
    return jwtToken;
  }

  public String getAccessToken() {
    return accessToken;
  }

  public String getRefreshToken() {
    return refreshToken;
  }

  public String getExpiresInSeconds() {
    return expiresInSeconds;
  }

  public String getXeroTenantId() {
    return xeroTenantId;
  }

  public void setJwtToken(String jwtToken) {
    this.jwtToken = jwtToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }

  public void setExpiresInSeconds(String expiresInSeconds) {
    this.expiresInSeconds = expiresInSeconds;
  }

  public void setXeroTenantId(String xeroTenantId) {
    this.xeroTenantId = xeroTenantId;
  }
}
