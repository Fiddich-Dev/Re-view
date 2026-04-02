package com.fiddich.review.question;

import com.fiddich.review.block.Block;
import com.fiddich.review.block.BlockService;
import com.fiddich.review.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestionService {

    public static final String ERR_QUESTION_NOT_FOUND = "존재하지 않는 문제입니다.";

    private final QuestionRepository questionRepository;
    private final BlockService blockService;

    public Question findById(Long questionId) {
        return questionRepository.findById(questionId)
                .orElseThrow(() -> new BusinessException(ERR_QUESTION_NOT_FOUND));
    }

    public List<Question> findActiveByBlock(Long blockId) {
        return questionRepository.findAllByBlockIdAndStatus(blockId, QuestionStatus.ACTIVE);
    }

    @Transactional
    public Question create(Long blockId, String content, String answer) {
        Block block = blockService.findById(blockId);
        return questionRepository.save(Question.builder()
                .block(block)
                .content(content)
                .answer(answer)
                .build());
    }

    @Transactional
    public void deactivate(Long questionId) {
        findById(questionId).deactivate();
    }
}
