package kr.co.lms.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 테넌트(고객사) VO
 * 
 * tb_tenant 테이블 매핑
 */
public class TenantVO implements Serializable {
    private static final long serialVersionUID = 1L;

    // 기본 정보
    private String tenantId;                // 고객사 고유 ID
    private String tenantNm;               // 고객사명
    private String tenantDesc;             // 고객사 설명

    // 담당자 정보
    private String contactName;            // 담당자명
    private String contactEmail;           // 담당자 이메일
    private String contactPhone;           // 담당자 전화번호

    // 구독 정보
    private String subscriptionStatus;     // 구독 상태 (ACTIVE/INACTIVE/SUSPENDED)
    private Integer maxUsers;              // 최대 사용자 수
    private Integer currentUsers;          // 현재 사용자 수
    private Long storageLimit;             // 최대 저장소 용량
    private Long usedStorage;              // 사용 중인 저장소 용량
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate subscriptionStartDt; // 구독 시작일
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate subscriptionEndDt;   // 구독 종료일

    // 타임스탬프 (API 응답에서 제외)
    @JsonIgnore
    private LocalDateTime regDt;           // 등록일
    @JsonIgnore
    private LocalDateTime updDt;           // 수정일

    // ============ Constructor ============
    public TenantVO() {
    }

    public TenantVO(String tenantId, String tenantNm) {
        this.tenantId = tenantId;
        this.tenantNm = tenantNm;
    }

    public TenantVO(String tenantId, String tenantNm, String tenantDesc) {
        this.tenantId = tenantId;
        this.tenantNm = tenantNm;
        this.tenantDesc = tenantDesc;
    }

    // ============ Getters and Setters ============
    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getTenantNm() {
        return tenantNm;
    }

    public void setTenantNm(String tenantNm) {
        this.tenantNm = tenantNm;
    }

    public String getTenantDesc() {
        return tenantDesc;
    }

    public void setTenantDesc(String tenantDesc) {
        this.tenantDesc = tenantDesc;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getSubscriptionStatus() {
        return subscriptionStatus;
    }

    public void setSubscriptionStatus(String subscriptionStatus) {
        this.subscriptionStatus = subscriptionStatus;
    }

    public Integer getMaxUsers() {
        return maxUsers;
    }

    public void setMaxUsers(Integer maxUsers) {
        this.maxUsers = maxUsers;
    }

    public Integer getCurrentUsers() {
        return currentUsers;
    }

    public void setCurrentUsers(Integer currentUsers) {
        this.currentUsers = currentUsers;
    }

    public Long getStorageLimit() {
        return storageLimit;
    }

    public void setStorageLimit(Long storageLimit) {
        this.storageLimit = storageLimit;
    }

    public Long getUsedStorage() {
        return usedStorage;
    }

    public void setUsedStorage(Long usedStorage) {
        this.usedStorage = usedStorage;
    }

    public LocalDate getSubscriptionStartDt() {
        return subscriptionStartDt;
    }

    public void setSubscriptionStartDt(LocalDate subscriptionStartDt) {
        this.subscriptionStartDt = subscriptionStartDt;
    }

    public LocalDate getSubscriptionEndDt() {
        return subscriptionEndDt;
    }

    public void setSubscriptionEndDt(LocalDate subscriptionEndDt) {
        this.subscriptionEndDt = subscriptionEndDt;
    }

    public LocalDateTime getRegDt() {
        return regDt;
    }

    public void setRegDt(LocalDateTime regDt) {
        this.regDt = regDt;
    }

    public LocalDateTime getUpdDt() {
        return updDt;
    }

    public void setUpdDt(LocalDateTime updDt) {
        this.updDt = updDt;
    }

    @Override
    public String toString() {
        return "TenantVO{" +
                "tenantId='" + tenantId + '\'' +
                ", tenantNm='" + tenantNm + '\'' +
                ", tenantDesc='" + tenantDesc + '\'' +
                ", subscriptionStatus='" + subscriptionStatus + '\'' +
                ", currentUsers=" + currentUsers +
                ", maxUsers=" + maxUsers +
                '}';
    }
}
