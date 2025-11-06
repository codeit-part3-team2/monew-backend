--뉴스/아티클(Article) 도메인
-- =========================
INSERT INTO articles (source, source_url, title, publish_date, summary, comment_count, view_count) VALUES
    ('Chosun','https://biz.chosun.com/it-science/ict/2025/10/16/FY3SOPSY65EKBLQLZALLF2GV6I/','AI 적용한 네이버 블로그, 초기 이용자 반응은', NOW() - INTERVAL '7 days','AI로 맞춤 콘텐츠 추천, 개편 한 달 반응',3,180),
    ('Chosun','https://www.chosun.com/economy/tech_it/2025/04/24/VMY3UPDHUNFK5HC6ZBSLJ6XX24/','네이버, 최신 AI 모델 오픈소스로 무료 공개', NOW() - INTERVAL '182 days','하이퍼클로바X 시드 모델 무료 제공',4,220),
    ('Chosun','https://www.chosun.com/economy/tech_it/2025/10/23/R3VTTXIJYJBSZECXSMIE6SUCYM/','벤 만 앤트로픽 공동 창업자, 한국은 가장 기대되는 AI 시장', NOW() - INTERVAL '1 days','클로드 개발사, 한국 AI 시장 높이 평가',5,240),
    ('Chosun','https://www.chosun.com/economy/tech_it/2025/10/23/4NOEW6JW3RF7VDN2HGKABN2U7U/','오픈AI, 한국 AI 리더십 위해 협력 강화 필요', NOW() - INTERVAL '1 days','소버린 AI와 글로벌 협력 듀얼 트랙 전략',4,195),
    ('Chosun','https://www.chosun.com/economy/money/2025/10/23/62DIWDGSRRGKHJYIORYU3Q5VB4/','양자컴·원전주, 거품 논란 속 급락·급등 반복', NOW() - INTERVAL '1 days','AI 열풍 타고 상승했던 주식 변동성',3,150),
    ('Chosun','https://www.chosun.com/economy/tech_it/2025/10/23/3OS6C7KDKJH5VCLB7DUFWO46TY/','AI로 플라스마 통제 쉬워져, 핵융합 발전 가까이', NOW() - INTERVAL '23 days','AI 기술로 핵융합 반응 정교한 제어',2,120),
    ('Chosun','https://www.chosun.com/economy/economy_general/2025/10/22/IWNF7IWUM5GRXBYQV3OWYCAI7M/','기술 특례 상장 82곳 중 48곳 주가 하락', NOW() - INTERVAL '2 days','부실 심사로 뻥튀기 상장 악용 지적',3,135),
    ('Naver','https://blog.naver.com/c1c1b1b1/224014877211','IT 일반, 네이버 언론사 제공 2025년 9월', NOW() - INTERVAL '33 days','구글 크롬 AI 제미나이 본격 적용',1,88),
    ('Naver','https://n.news.naver.com/mnews/article/277/0005521038','SK하이닉스, 3분기 영업이익 7조 전망…HBM 수요 견조', NOW() - INTERVAL '8 days','고대역폭메모리 공급 확대로 실적 개선',4,230),
    ('Naver','https://n.news.naver.com/mnews/article/421/0007885042','삼성전자, AI 반도체 수요 회복 기대감…주가 상승', NOW() - INTERVAL '5 days','메모리 가격 반등으로 실적 턴어라운드',3,190),
    ('Naver','https://n.news.naver.com/mnews/article/011/0004402211','네이버 하이퍼클로바X, 기업용 AI 솔루션 확대', NOW() - INTERVAL '12 days','엔터프라이즈 시장 공략 본격화',5,270),
    ('Naver','https://n.news.naver.com/mnews/article/008/0005115233','현대차, 자율주행 AI 기술 개발 박차…미국 투자 확대', NOW() - INTERVAL '6 days','소프트웨어 정의 차량 개발 가속',2,145),
    ('Naver','https://n.news.naver.com/mnews/article/366/0001057889','LG에너지솔루션, 배터리 AI 품질검사 시스템 도입', NOW() - INTERVAL '9 days','불량률 20% 감소 효과 확인',3,175),
    ('Naver','https://n.news.naver.com/mnews/article/018/0005911142','카카오, 생성형 AI 카카오아이 공개…톡·뮤직 연동', NOW() - INTERVAL '14 days','맞춤형 콘텐츠 큐레이션 강화',4,210),
    ('Naver','https://n.news.naver.com/mnews/article/001/0015203344','금융위, AI 기반 불법금융 탐지 시스템 가동', NOW() - INTERVAL '7 days','보이스피싱·자금세탁 실시간 차단',2,130),
    ('Naver','https://n.news.naver.com/mnews/article/015/0005099221','코스피 2900선 회복…외국인 반도체주 매수 지속', NOW() - INTERVAL '3 days','AI 수혜주 중심 상승세',5,290),
    ('Naver','https://n.news.naver.com/mnews/article/277/0005520011','KT, AI 데이터센터 5000억 투자…2026년 완공', NOW() - INTERVAL '10 days','인천 송도에 초거대 AI 인프라 구축',3,165),
    ('Naver','https://n.news.naver.com/mnews/article/052/0002200345','포스코, AI 기반 스마트공장 확대…탄소배출 10% 감축', NOW() - INTERVAL '11 days','공정 최적화로 친환경 생산 달성',2,120),
    ('Naver','https://n.news.naver.com/article/001/0015666237','뉴욕증시, 고조되는 AI 거품론과 셧다운 우려 부각', NOW() - INTERVAL '18 days','시장 조정 압력 증가하며 차익실현 나타나',3,165),
    ('Naver','https://n.news.naver.com/mnews/article/421/0008526173','서버실 갇힌 AI는 끝, 산업 현장 뛰어든 피지컬 AI', NOW() - INTERVAL '18 days','제조·물류 등 현장에서 실물 작업하는 AI 본격화',4,210);

-- =========================
INSERT INTO interest_articles (interest_id, article_id) VALUES
    (1,1), (1,4), (1,5),
    (2,2), (2,14), (2,10), (2,1), (2,15),
    (3,3),
    (4,4), (4,5),
    (5,1), (5,2), (5,3), (5,4), (5,5), (5,6), (5,7), (5,8), (5,9), (5,10), (5,11), (5,12), (5,13), (5,14), (5,15),
    (6,1), (6,11), (6,2), (6,3), (6,4), (6,5), (6,6), (6,7), (6,8),
    (7,7), (7,10), (7,1), (7,15),
    (8,1), (8,11), (8,2), (8,3), (8,4), (8,5), (8,14), (8,6), (8,7), (8,8),
    (9,11),(9,1), (9,14), (9,2), (9,3), (9,4), (9,5), (9,6), (9,7), (9,8), (9,9),
    (10,1);