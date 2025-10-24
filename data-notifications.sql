--알림(Notification) 도메인
-- =========================
INSERT INTO notifications (user_id, content, resource_type, resource_id, confirmed) VALUES
(1, '구독 관심사에 새 기사 등록', 'article', 1, FALSE),
(1, '내 댓글에 새 좋아요',        'comment', 1, TRUE),
(2, '팔로우 관심사에 새 기사',     'article', 2, FALSE),
(3, '새 댓글 알림',               'comment', 3, TRUE),
(5, '관심 기사 업데이트',          'article', 5, TRUE),
(1,'새 댓글이 달렸습니다.','comment',10, FALSE),
(4,'환경 소식 알림','ARTICLE',4,TRUE),
(5,'스타트업 소식','ARTICLE',5,FALSE),
(6,'금융 뉴스 도착','ARTICLE',6,FALSE),
(7,'정치 관련 소식','ARTICLE',7,TRUE),
(8,'국제 이슈 속보','ARTICLE',8,FALSE),
(9,'스포츠 뉴스 업데이트','ARTICLE',9,TRUE),
(10,'문화 기사 알림','ARTICLE',10,FALSE),
(11,'기술 관련 업데이트','ARTICLE',11,TRUE),
(12,'자동차 산업 소식','ARTICLE',12,FALSE),
(13,'여행 기사 등록','ARTICLE',13,TRUE),
(14,'건강 정보 도착','ARTICLE',14,FALSE),
(15,'교육 소식 알림','ARTICLE',15,TRUE),

;