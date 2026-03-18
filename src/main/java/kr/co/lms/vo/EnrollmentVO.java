package kr.co.lms.vo;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 수강 VO (Value Object)
 * 
 * 데이터베이스의 tb_enrollment 테이블에 매핑되는 값 객체
 * 요청/응답에도 동일하게 사용
 */
public class EnrollmentVO implements Serializable {
    private static final long serialVersionUID = 1L;

    // 기본 정보
    private String enrollmentId;
    private String userId;
    private String courseId;
    private String enrollmentStatus;
    private Float completionRate;
    private LocalDateTime enrollmentDt;
    private LocalDateTime completionDt;
    private LocalDateTime regDt;
    private LocalDateTime updDt;

    // 추가 정보 (select 쿼리에서 join)
    private String userName;
    private String courseNm;

    // Constructors
    public EnrollmentVO() {
    }

    public EnrollmentVO(String userId, String courseId) {
        this.userId = userId;
        this.courseId = courseId;
        this.enrollmentStatus = "ENROLL";
        this.completionRate = 0.0f;
        this.enrollmentDt = LocalDateTime.now();
        this.regDt = LocalDateTime.now();
        this.updDt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(String enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getEnrollmentStatus() {
        return enrollmentStatus;
    }

    public void setEnrollmentStatus(String enrollmentStatus) {
        this.enrollmentStatus = enrollmentStatus;
    }

    public Float getCompletionRate() {
        return completionRate;
    }

    public void setCompletionRate(Float completionRate) {
        this.completionRate = completionRate;
    }

    public LocalDateTime getEnrollmentDt() {
        return enrollmentDt;
    }

    public void setEnrollmentDt(LocalDateTime enrollmentDt) {
        this.enrollmentDt = enrollmentDt;
    }

    public LocalDateTime getCompletionDt() {
        return completionDt;
    }

    public void setCompletionDt(LocalDateTime completionDt) {
        this.completionDt = completionDt;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCourseNm() {
        return courseNm;
    }

    public void setCourseNm(String courseNm) {
        this.courseNm = courseNm;
    }

    @Override
    public String toString() {
        return "EnrollmentVO{" +
                "enrollmentId='" + enrollmentId + '\'' +
                ", userId='" + userId + '\'' +
                ", courseId='" + courseId + '\'' +
                ", enrollmentStatus='" + enrollmentStatus + '\'' +
                ", completionRate=" + completionRate +
                '}';
    }
}
