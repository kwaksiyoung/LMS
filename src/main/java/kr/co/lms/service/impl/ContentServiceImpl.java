package kr.co.lms.service.impl;

import kr.co.lms.mapper.ContentMapper;
import kr.co.lms.service.ContentService;
import kr.co.lms.vo.ContentVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 콘텐츠 관리 Service 구현
 */
@Service
@Transactional
@RequiredArgsConstructor
public class ContentServiceImpl implements ContentService {

    private static final Logger logger = LoggerFactory.getLogger(ContentServiceImpl.class);

    private final ContentMapper contentMapper;

    /**
     * 콘텐츠 조회 (ID로)
     */
    @Override
    @Transactional(readOnly = true)
    public ContentVO selectContent(String contentId) {
        logger.debug("콘텐츠 조회: contentId={}", contentId);
        return contentMapper.selectContent(contentId);
    }

    /**
     * 콘텐츠 목록 조회
     */
    @Override
    @Transactional(readOnly = true)
    public List<ContentVO> selectContentList(ContentVO contentVO) {
        logger.debug("콘텐츠 목록 조회: {}", contentVO);
        return contentMapper.selectContentList(contentVO);
    }

    /**
     * 콘텐츠 등록
     */
    @Override
    public int insertContent(ContentVO contentVO) {
        logger.info("콘텐츠 등록: contentId={}", contentVO.getContentId());
        int result = contentMapper.insertContent(contentVO);
        if (result > 0) {
            logger.info("콘텐츠 등록 성공: contentId={}", contentVO.getContentId());
        } else {
            logger.warn("콘텐츠 등록 실패: contentId={}", contentVO.getContentId());
        }
        return result;
    }

    /**
     * 콘텐츠 수정
     */
    @Override
    public int updateContent(ContentVO contentVO) {
        logger.info("콘텐츠 수정: contentId={}", contentVO.getContentId());
        int result = contentMapper.updateContent(contentVO);
        if (result > 0) {
            logger.info("콘텐츠 수정 성공: contentId={}", contentVO.getContentId());
        } else {
            logger.warn("콘텐츠 수정 실패: contentId={}", contentVO.getContentId());
        }
        return result;
    }

    /**
     * 콘텐츠 삭제
     */
    @Override
    public int deleteContent(ContentVO contentVO) {
        logger.info("콘텐츠 삭제: contentId={}, tenantId={}", contentVO.getContentId(), contentVO.getTenantId());
        int result = contentMapper.deleteContent(contentVO);
        if (result > 0) {
            logger.info("콘텐츠 삭제 성공: contentId={}", contentVO.getContentId());
        } else {
            logger.warn("콘텐츠 삭제 실패: contentId={}", contentVO.getContentId());
        }
        return result;
    }

    /**
     * 콘텐츠 수 조회
     */
    @Override
    @Transactional(readOnly = true)
    public int selectContentCount(ContentVO contentVO) {
        logger.debug("콘텐츠 수 조회");
        return contentMapper.selectContentCount(contentVO);
    }

    /**
     * 과정별 콘텐츠 조회
     */
    @Override
    @Transactional(readOnly = true)
    public List<ContentVO> selectContentsByCourseId(String courseId) {
        logger.debug("과정별 콘텐츠 조회: courseId={}", courseId);
        return contentMapper.selectContentsByCourseId(courseId);
    }
}
