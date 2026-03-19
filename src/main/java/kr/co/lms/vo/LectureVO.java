package kr.co.lms.vo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 강의(단원) VO (Value Object)
 *
 * 데이터베이스의 tb_lecture 테이블에 매핑되는 값 객체
 * 과정과 독립적으로 관리되는 강의(단원) 정보
 */
public class LectureVO implements Serializable {
  private static final long serialVersionUID = 1L;

  // 기본 정보
  private String lectureId;           // PK
  private String tenantId;            // PK (멀티테넌시)
  private String lectureNm;           // 강의명 (필수)
  private String lectureDesc;         // 강의 설명
  private Integer durationMinutes;    // 강의 총 시간 (분)
  private String lectureType = "REQUIRED";         // 차시 유형 (REQUIRED: 필수, OPTIONAL: 선택, 기본값: "REQUIRED")
  private String useYn;               // 사용여부 (기본값: "Y")
  private LocalDateTime regDt;        // 등록일
  private LocalDateTime updDt;        // 수정일

  // 관계 데이터 (조회 시 포함)
  private List<ContentVO> contents;   // 강의에 속한 콘텐츠 목록
  private Integer contentCount;       // 콘텐츠 개수

  // 검색/필터링/페이징 필드
  private String searchKeyword;       // 검색어 (강의명, 설명)
  private String lectureTypeFilter;   // 차시 유형 필터 (REQUIRED/OPTIONAL)
  private Integer startRow;           // 페이징 시작 행 (LIMIT offset)
  private Integer pageSize;           // 페이징 크기 (LIMIT count, 기본값: 10)
  private Integer totalCount;         // 검색 결과 총 개수
  private Integer totalPages;         // 검색 결과 총 페이지 수
  private Integer currentPage;        // 현재 페이지

  // Constructors
  public LectureVO() {
  }

  public LectureVO(String lectureNm) {
    this.lectureNm = lectureNm;
    this.lectureType = "REQUIRED";
    this.useYn = "Y";
    this.regDt = LocalDateTime.now();
    this.updDt = LocalDateTime.now();
  }

  public LectureVO(String lectureId, String tenantId, String lectureNm) {
    this.lectureId = lectureId;
    this.tenantId = tenantId;
    this.lectureNm = lectureNm;
    this.lectureType = "REQUIRED";
    this.useYn = "Y";
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

  public String getTenantId() {
    return tenantId;
  }

  public void setTenantId(String tenantId) {
    this.tenantId = tenantId;
  }

  public String getLectureNm() {
    return lectureNm;
  }

  public void setLectureNm(String lectureNm) {
    this.lectureNm = lectureNm;
  }

  public String getLectureDesc() {
    return lectureDesc;
  }

  public void setLectureDesc(String lectureDesc) {
    this.lectureDesc = lectureDesc;
  }

  public Integer getDurationMinutes() {
    return durationMinutes;
  }

  public void setDurationMinutes(Integer durationMinutes) {
    this.durationMinutes = durationMinutes;
  }

  public String getLectureType() {
    return lectureType;
  }

  public void setLectureType(String lectureType) {
    this.lectureType = lectureType;
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

  public List<ContentVO> getContents() {
    return contents;
  }

  public void setContents(List<ContentVO> contents) {
    this.contents = contents;
  }

  public Integer getContentCount() {
    return contentCount;
  }

  public void setContentCount(Integer contentCount) {
    this.contentCount = contentCount;
  }

  public String getSearchKeyword() {
    return searchKeyword;
  }

  public void setSearchKeyword(String searchKeyword) {
    this.searchKeyword = searchKeyword;
  }

  public String getLectureTypeFilter() {
    return lectureTypeFilter;
  }

  public void setLectureTypeFilter(String lectureTypeFilter) {
    this.lectureTypeFilter = lectureTypeFilter;
  }

  public Integer getStartRow() {
    return startRow;
  }

  public void setStartRow(Integer startRow) {
    this.startRow = startRow;
  }

  public Integer getPageSize() {
    if (pageSize == null) {
      pageSize = 10; // 기본값: 10개
    }
    return pageSize;
  }

  public void setPageSize(Integer pageSize) {
    this.pageSize = pageSize;
  }

  public Integer getTotalCount() {
    return totalCount;
  }

  public void setTotalCount(Integer totalCount) {
    this.totalCount = totalCount;
    // 총 페이지 수 계산
    if (totalCount != null && pageSize != null && pageSize > 0) {
      this.totalPages = (int) Math.ceil((double) totalCount / pageSize);
    }
  }

  public Integer getTotalPages() {
    return totalPages;
  }

  public void setTotalPages(Integer totalPages) {
    this.totalPages = totalPages;
  }

  public Integer getCurrentPage() {
    return currentPage;
  }

  public void setCurrentPage(Integer currentPage) {
    this.currentPage = currentPage;
  }

  @Override
  public String toString() {
    return "LectureVO{" +
        "lectureId='" + lectureId + '\'' +
        ", tenantId='" + tenantId + '\'' +
        ", lectureNm='" + lectureNm + '\'' +
        ", durationMinutes=" + durationMinutes +
        ", lectureType='" + lectureType + '\'' +
        ", useYn='" + useYn + '\'' +
        '}';
  }
}
