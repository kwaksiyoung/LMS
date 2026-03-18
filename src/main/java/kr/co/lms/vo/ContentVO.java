package kr.co.lms.vo;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 콘텐츠 VO (Value Object)
 * 
 * 데이터베이스의 tb_content 테이블에 매핑되는 값 객체
 * 요청/응답에도 동일하게 사용
 */
public class ContentVO implements Serializable {
    private static final long serialVersionUID = 1L;

    // 기본 정보
    private String contentId;
    private String tenantId;        // 테넌트 ID (멀티테넌시)
    private String courseId;
    private String contentTitle;
    private String contentType;
    private String contentUrl;
    private String contentDesc;
    private Integer contentOrder;
    private Integer durationMinutes; // 콘텐츠 길이 (분)
    private String useYn;
    private LocalDateTime regDt;
    private LocalDateTime updDt;

    // Constructors
    public ContentVO() {
    }

    public ContentVO(String courseId, String contentTitle) {
        this.courseId = courseId;
        this.contentTitle = contentTitle;
        this.useYn = "Y";
        this.regDt = LocalDateTime.now();
        this.updDt = LocalDateTime.now();
    }

    public ContentVO(String courseId, String contentTitle, String contentType) {
        this.courseId = courseId;
        this.contentTitle = contentTitle;
        this.contentType = contentType;
        this.useYn = "Y";
        this.regDt = LocalDateTime.now();
        this.updDt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getContentTitle() {
        return contentTitle;
    }

    public void setContentTitle(String contentTitle) {
        this.contentTitle = contentTitle;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }

    public String getContentDesc() {
        return contentDesc;
    }

    public void setContentDesc(String contentDesc) {
        this.contentDesc = contentDesc;
    }

    public Integer getContentOrder() {
        return contentOrder;
    }

    public void setContentOrder(Integer contentOrder) {
        this.contentOrder = contentOrder;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getUseYn() {
        return useYn;
    }

    public void setUseYn(String useYn) {
        this.useYn = useYn;
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
        return "ContentVO{" +
                "contentId='" + contentId + '\'' +
                ", tenantId='" + tenantId + '\'' +
                ", courseId='" + courseId + '\'' +
                ", contentTitle='" + contentTitle + '\'' +
                ", contentType='" + contentType + '\'' +
                ", contentOrder=" + contentOrder +
                ", durationMinutes=" + durationMinutes +
                '}';
    }
}
