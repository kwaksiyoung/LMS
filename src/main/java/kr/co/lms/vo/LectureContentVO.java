package kr.co.lms.vo;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 강의-콘텐츠 매핑 VO (Value Object)
 * 
 * 데이터베이스의 tb_lecture_content 테이블에 매핑되는 값 객체
 * 강의와 콘텐츠 간의 M:N 관계를 표현
 */
public class LectureContentVO implements Serializable {
    private static final long serialVersionUID = 1L;

    // 기본 정보
    private String lectureId;
    private String contentId;
    private String tenantId;
    private Integer contentOrder;
    private LocalDateTime regDt;
    private LocalDateTime updDt;

    // Constructors
    public LectureContentVO() {
    }

    public LectureContentVO(String lectureId, String contentId, String tenantId) {
        this.lectureId = lectureId;
        this.contentId = contentId;
        this.tenantId = tenantId;
        this.contentOrder = 0;
        this.regDt = LocalDateTime.now();
        this.updDt = LocalDateTime.now();
    }

    public LectureContentVO(String lectureId, String contentId, String tenantId, Integer contentOrder) {
        this.lectureId = lectureId;
        this.contentId = contentId;
        this.tenantId = tenantId;
        this.contentOrder = contentOrder;
        this.regDt = LocalDateTime.now();
        this.updDt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getLectureId() {
        return lectureId;
    }

    public void setLectureId(String lectureId) {
        this.lectureId = lectureId;
    }

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

    public Integer getContentOrder() {
        return contentOrder;
    }

    public void setContentOrder(Integer contentOrder) {
        this.contentOrder = contentOrder;
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
        return "LectureContentVO{" +
                "lectureId='" + lectureId + '\'' +
                ", contentId='" + contentId + '\'' +
                ", tenantId='" + tenantId + '\'' +
                ", contentOrder=" + contentOrder +
                '}';
    }
}
