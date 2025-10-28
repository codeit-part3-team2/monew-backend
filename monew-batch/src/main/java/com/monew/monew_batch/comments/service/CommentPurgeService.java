package com.monew.monew_batch.comments.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentPurgeService {

	@PersistenceContext
	private final EntityManager em;

	private static final int BATCH_SIZE = 500;

	@Transactional
	public int purge(LocalDateTime cutoff) {
		int totalDeleted = 0;

		while (true) {
			List<Long> ids = em.createQuery(
					"select c.id from Comment c " +
						"where c.deleted = true and c.updatedAt < :cutoff", Long.class)
				.setParameter("cutoff", cutoff)
				.setMaxResults(BATCH_SIZE)
				.getResultList();

			if (ids.isEmpty()) break;

			em.createQuery("delete from CommentLike cl where cl.comment.id in :ids")
				.setParameter("ids", ids)
				.executeUpdate();

			int deleted = em.createQuery("delete from Comment c where c.id in :ids")
				.setParameter("ids", ids)
				.executeUpdate();

			totalDeleted += deleted;
			em.clear();
		}
		log.info("[PURGE][COMMENT] cutoff={}, deleted={}", cutoff, totalDeleted);
		return totalDeleted;
	}

}
