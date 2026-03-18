package kr.co.lms.vo;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 과정 VO (Value Object)
 */
public class CourseVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String courseId;
    private String courseNm;
    private String courseDesc;
    private String instructorId;
    private String instructorNm;
    private LocalDate startDt;
    private LocalDate endDt;
    private Integer maxStudents;
    private Integer currentStudents;
    private String status;
    private String useYn;
    private LocalDateTime regDt;
    private LocalDateTime updDt;

    // Constructors
    public CourseVO() {
    }

    public CourseVO(String courseId, String courseNm) {
        this.courseId = courseId;
        this.courseNm = courseNm;
        this.status = "DRAFT";
        this.useYn = "Y";
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

    public String getCourseNm() {
        return courseNm;
    }

    public void setCourseNm(String courseNm) {
        this.courseNm = courseNm;
    }

    public String getCourseDesc() {
        return courseDesc;
    }

    public void setCourseDesc(String courseDesc) {
        this.courseDesc = courseDesc;
    }

    public String getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(String instructorId) {
        this.instructorId = instructorId;
    }

    public String getInstructorNm() {
        return instructorNm;
    }

    public void setInstructorNm(String instructorNm) {
        this.instructorNm = instructorNm;
    }

    public LocalDate getStartDt() {
        return startDt;
    }

    public void setStartDt(LocalDate startDt) {
        this.startDt = startDt;
    }

    public LocalDate getEndDt() {
        return endDt;
    }

    public void setEndDt(LocalDate endDt) {
        this.endDt = endDt;
    }

    public Integer getMaxStudents() {
        return maxStudents;
    }

    public void setMaxStudents(Integer maxStudents) {
        this.maxStudents = maxStudents;
    }

    public Integer getCurrentStudents() {
        return currentStudents;
    }

    public void setCurrentStudents(Integer currentStudents) {
        this.currentStudents = currentStudents;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
        return "CourseVO{" +
                "courseId='" + courseId + '\'' +
                ", courseNm='" + courseNm + '\'' +
                ", instructorId='" + instructorId + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
