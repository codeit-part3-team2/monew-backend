package com.monew.monew_api.Comment;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.monew.monew_api.comments.repository.CommentRepository;
import com.monew.monew_api.comments.service.CommentService;
import com.monew.monew_api.common.exception.comment.CommentNotFoundException;

/**
 * 물리 삭제 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CommentService - hardDelete")
public class CommentServiceHardDeleteTest {

	@InjectMocks
	private CommentService commentService;

	@Mock
	private CommentRepository commentRepository;

	// 물리 삭제 성공 - is_deleted=true인 댓글
	@Test
	void hardDelete_Success_DeletedComment() {

		// given
		Long commentId = 1L;
		given(commentRepository.hardDeleteById(commentId)).willReturn(1); // 1개 삭제됨

		// when
		commentService.hardDelete(commentId);

		// then
		then(commentRepository).should().hardDeleteById(commentId);
	}

	// 물리 삭제 실패 - 존재하지 않는 댓글
	@Test
	void hardDelete_CommentNotFound() {

		// given
		Long commentId = 999L;
		given(commentRepository.hardDeleteById(commentId)).willReturn(0); // 삭제된 row 없음

		// when & then
		assertThatThrownBy(() -> commentService.hardDelete(commentId))
			.isInstanceOf(CommentNotFoundException.class);

		then(commentRepository).should().hardDeleteById(commentId);
	}

	// 물리 삭제 실패 - is_deleted=false인 댓글 (논리 삭제 안됨)
	@Test
	void hardDelete_NotDeletedComment() {

		// given
		Long commentId = 2L;
		// is_deleted=false 삭제 조건에 안 맞음 → 0개 삭제
		given(commentRepository.hardDeleteById(commentId)).willReturn(0);

		// when & then
		assertThatThrownBy(() -> commentService.hardDelete(commentId))
			.isInstanceOf(CommentNotFoundException.class);

		then(commentRepository).should().hardDeleteById(commentId);
	}
}
