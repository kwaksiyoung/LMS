package kr.co.lms.vo;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 암호화 키 VO (Value Object)
 * 
 * 데이터베이스의 tb_encryption_key 테이블에 매핑
 * 테넌트별 개인정보 암호화를 위한 AES-256 키를 관리
 */
public class EncryptionKeyVO implements Serializable {
    private static final long serialVersionUID = 1L;

    // 기본 정보
    private String keyId;              // 암호화 키 ID (자동 생성)
    private String tenantId;           // 고객사 ID
    private String keyName;            // 키 이름 (버전 관리용, ex: AES-256-GCM-v1)
    private String encryptedKey;       // Base64 인코딩된 암호화 키
    private String algorithm;          // 암호화 알고리즘 (AES-256-GCM)
    private Integer keySize;           // 키 길이 (비트, 256)
    private String isActive;           // 활성 여부 (Y/N)
    private String rotationPolicy;     // 키 로테이션 정책
    private LocalDateTime rotatedDt;   // 마지막 로테이션 일시
    private LocalDateTime regDt;       // 등록일
    private LocalDateTime updDt;       // 수정일

    // Constructors
    public EncryptionKeyVO() {
    }

    public EncryptionKeyVO(String tenantId, String keyName, String encryptedKey) {
        this.tenantId = tenantId;
        this.keyName = keyName;
        this.encryptedKey = encryptedKey;
        this.algorithm = "AES-256-GCM";
        this.keySize = 256;
        this.isActive = "Y";
        this.regDt = LocalDateTime.now();
        this.updDt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public String getEncryptedKey() {
        return encryptedKey;
    }

    public void setEncryptedKey(String encryptedKey) {
        this.encryptedKey = encryptedKey;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public Integer getKeySize() {
        return keySize;
    }

    public void setKeySize(Integer keySize) {
        this.keySize = keySize;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public String getRotationPolicy() {
        return rotationPolicy;
    }

    public void setRotationPolicy(String rotationPolicy) {
        this.rotationPolicy = rotationPolicy;
    }

    public LocalDateTime getRotatedDt() {
        return rotatedDt;
    }

    public void setRotatedDt(LocalDateTime rotatedDt) {
        this.rotatedDt = rotatedDt;
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
        return "EncryptionKeyVO{" +
                "keyId='" + keyId + '\'' +
                ", tenantId='" + tenantId + '\'' +
                ", keyName='" + keyName + '\'' +
                ", algorithm='" + algorithm + '\'' +
                ", keySize=" + keySize +
                ", isActive='" + isActive + '\'' +
                ", rotatedDt=" + rotatedDt +
                '}';
    }
}
