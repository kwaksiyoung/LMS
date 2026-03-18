package kr.co.lms.vo;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 과정-강의 매핑 VO (Value Object)
 * 
 * 데이터베이스의 tb_course_lecture 테이블에 매핑되는 값 객체
 * 과정과 강의 간의 M:N 관계를 표현
 */
public class CourseLectureVO implements Serializable {
    private static final long serialVersionUID = 1L;

    // 기본 정보
    private String courseId;
    private String lectureId;
    private String tenantId;
    private Integer lectureOrder;
    private LocalDateTime regDt;
    private LocalDateTime updDt;

    // Constructors
    public CourseLectureVO() {
    }

    public CourseLectureVO(String courseId, String lectureId, String tenantId) {
        this.courseId = courseId;
        this.lectureId = lectureId;
        this.tenantId = tenantId;
        this.lectureOrder = 0;
        this.regDt = LocalDateTime.now();
        this.updDt = LocalDateTime.now();
    }

    public CourseLectureVO(String courseId, String lectureId, String tenantId, Integer lectureOrder) {
        this.courseId = courseId;
        this.lectureId = lectureId;
        this.tenantId = tenantId;
        this.lectureOrder = lectureOrder;
        this.regDt = LocalDateTime.now();
        this.updDt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getLectureId() {
        return lectureId;
    }

    public void setLectureId(String lectureId) {
        this.lectureId = lectureId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public Integer getLectureOrder() {
        return lectureOrder;
    }

    public void setLectureOrder(Integer lectureOrder) {
        this.lectureOrder = lectureOrder;
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
        return "CourseLectureVO{" +
                "courseId='" + courseId + '\'' +
                ", lectureId='" + lectureId + '\'' +
                ", tenantId='" + tenantId + '\'' +
                ", lectureOrder=" + lectureOrder +
                '}';
    }
}
