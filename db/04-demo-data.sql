-- A-Rate 데모 데이터 삽입
-- 기존 테이블 구조에 맞춰 데모 데이터를 추가합니다.

USE arate_db;

-- 데모 사용자 삽입 (학생들) - 중복되지 않는 이메일 사용
INSERT INTO users (name, email, nickname, department, role, student_number, email_verified, provider) VALUES
('김지훈', 'demo.jihun@ajou.ac.kr', '지훈이', '소프트웨어학과', 'STUDENT', '202012345', TRUE, 'GOOGLE'),
('박유진', 'demo.yujin@ajou.ac.kr', '유진이', '컴퓨터공학과', 'STUDENT', '202012346', TRUE, 'GOOGLE'),
('이서연', 'demo.seoyeon@ajou.ac.kr', '서연이', '정보통신대학', 'STUDENT', '202012347', TRUE, 'GOOGLE'),
('최민호', 'demo.minho@ajou.ac.kr', '민호', '소프트웨어학과', 'STUDENT', '202012348', TRUE, 'GOOGLE'),
('정하늘', 'demo.haneul@ajou.ac.kr', '하늘이', '컴퓨터공학과', 'STUDENT', '202012349', TRUE, 'GOOGLE');

-- 기존 강의에 대한 데모 강의평가 삽입 (실제 존재하는 강의 ID 사용)
-- 새로 추가된 사용자들의 ID는 자동 증가되므로 마지막 ID부터 사용
INSERT INTO lecture_evaluations (
    user_id, lecture_id, semester, content, 
    delivery_score, expertise_score, generosity_score, effectiveness_score, character_score, difficulty_score,
    assignment_amount, assignment_difficulty, exam, team_project
) VALUES
-- 강의 ID 17에 대한 평가들 (새로 추가된 사용자들 사용)
((SELECT MAX(id)-4 FROM users), 17, '2024-1', '설명이 친절하고 과제도 많지 않아서 좋았어요. 교수님이 학생들을 배려해주시는 게 느껴졌습니다.', 
 5, 4, 5, 4, 5, 3, 'FEW', 'NORMAL', 'BOTH', FALSE),

((SELECT MAX(id)-3 FROM users), 17, '2024-1', '전반적으로 무난하지만 조별과제가 좀 아쉬워요. 그래도 많이 배웠습니다.', 
 4, 4, 4, 4, 4, 2, 'NORMAL', 'EASY', 'BOTH', TRUE),

((SELECT MAX(id)-2 FROM users), 17, '2024-1', '처음 접하는 분야였는데 차근차근 설명해주셔서 이해하기 쉬웠어요!', 
 5, 5, 5, 5, 5, 2, 'FEW', 'EASY', 'FINAL', FALSE),

-- 강의 ID 25에 대한 평가들  
((SELECT MAX(id)-4 FROM users), 25, '2024-1', '강의 내용이 아주 알차고 유익했습니다. 실습 위주로 진행되어서 좋았어요.', 
 5, 5, 4, 5, 4, 4, 'MANY', 'HARD', 'BOTH', TRUE),

((SELECT MAX(id)-1 FROM users), 25, '2024-1', '조금 어려웠지만 얻어가는 게 많았어요. 기초를 탄탄히 다질 수 있었습니다.', 
 4, 5, 4, 5, 4, 4, 'MANY', 'NORMAL', 'BOTH', FALSE),

((SELECT MAX(id) FROM users), 25, '2024-1', '과제가 많긴 하지만 그만큼 실력이 늘어나는 게 느껴져요. 추천합니다!', 
 4, 5, 3, 4, 4, 5, 'MANY', 'HARD', 'MIDTERM', TRUE),

-- 강의 ID 27에 대한 평가들
((SELECT MAX(id)-3 FROM users), 27, '2024-1', '수업 템포가 빠르지만 교수님은 열정적이에요. 실력이 많이 늘었습니다.', 
 3, 4, 4, 3, 4, 3, 'NORMAL', 'NORMAL', 'BOTH', FALSE),

((SELECT MAX(id)-2 FROM users), 27, '2024-1', '추천합니다! 개념 정리가 정말 잘 되어 있어요. 핵심을 제대로 배웠습니다.', 
 5, 5, 5, 5, 5, 3, 'NORMAL', 'NORMAL', 'BOTH', TRUE),

((SELECT MAX(id)-1 FROM users), 27, '2024-1', '처음엔 어려웠지만 점점 재미있어졌어요. 실력이 확실히 늘었습니다.', 
 4, 4, 4, 4, 4, 3, 'FEW', 'NORMAL', 'FINAL', FALSE),

((SELECT MAX(id) FROM users), 27, '2024-1', '과제를 통해 많이 배울 수 있었어요. 실무에서도 도움이 될 것 같습니다.', 
 4, 5, 4, 4, 5, 4, 'NORMAL', 'HARD', 'BOTH', TRUE);

-- 수강 인증 데모 데이터 삽입 (실제 존재하는 강의 ID 사용)
INSERT INTO enrollments (student_id, lecture_id, grade, is_certified, semester, certified_at) VALUES
((SELECT MAX(id)-4 FROM users), 17, 'A+', TRUE, '2024-1', NOW()),
((SELECT MAX(id)-3 FROM users), 17, 'A0', TRUE, '2024-1', NOW()),
((SELECT MAX(id)-2 FROM users), 17, 'A+', TRUE, '2024-1', NOW()),
((SELECT MAX(id)-4 FROM users), 25, 'B+', TRUE, '2024-1', NOW()),
((SELECT MAX(id)-1 FROM users), 25, 'A0', TRUE, '2024-1', NOW()),
((SELECT MAX(id) FROM users), 25, 'B0', TRUE, '2024-1', NOW()),
((SELECT MAX(id)-3 FROM users), 27, 'A0', TRUE, '2024-1', NOW()),
((SELECT MAX(id)-2 FROM users), 27, 'A+', TRUE, '2024-1', NOW()),
((SELECT MAX(id)-1 FROM users), 27, 'B+', TRUE, '2024-1', NOW()),
((SELECT MAX(id) FROM users), 27, 'A0', TRUE, '2024-1', NOW()); 