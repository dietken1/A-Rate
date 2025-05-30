-- 테이블 생성

DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS lectures;
DROP TABLE IF EXISTS evaluations;

CREATE TABLE users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    email TEXT NOT NULL,
    role TEXT NOT NULL  -- 예: 'student', 'professor'
);

CREATE TABLE lectures (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    professor_id INTEGER,
    FOREIGN KEY (professor_id) REFERENCES users(id)
);

CREATE TABLE evaluations (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER,
    lecture_id INTEGER,
    semester TEXT,
    delivery_score INTEGER,
    expertise_score INTEGER,
    fairness_score INTEGER,
    effectiveness_score INTEGER,
    personality_score INTEGER,
    difficulty_score INTEGER,
    comment TEXT,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (lecture_id) REFERENCES lectures(id)
);

-- 사용자 삽입

INSERT INTO users (name, email, role) VALUES
('김지훈', 'jihun@ajou.ac.kr', 'student'),
('박유진', 'yujin@ajou.ac.kr', 'student'),
('이서연', 'seoyeon@ajou.ac.kr', 'student'),
('정민수', 'msjung@ajou.ac.kr', 'professor'),
('오지현', 'jh.oh@ajou.ac.kr', 'professor');

-- 강의 삽입

INSERT INTO lectures (title, professor_id) VALUES
('오픈소스SW입문', 4),
('데이터사이언스개론', 5),
('객체지향프로그래밍', 4);

-- 강의평가 삽입

INSERT INTO evaluations (user_id, lecture_id, semester, delivery_score, expertise_score, fairness_score, effectiveness_score, personality_score, difficulty_score, comment) VALUES
(1, 1, '2023-1', 5, 4, 5, 4, 5, 3, '설명이 친절하고 과제도 많지 않아서 좋았어요.'),
(2, 1, '2023-1', 4, 4, 4, 4, 4, 2, '전반적으로 무난하지만 조별과제가 좀 아쉬워요.'),
(3, 2, '2023-2', 5, 5, 5, 5, 5, 4, '강의 내용이 아주 알차고 유익했습니다.'),
(1, 2, '2023-2', 4, 5, 4, 5, 4, 4, '조금 어려웠지만 얻어가는 게 많았어요.'),
(2, 3, '2024-1', 3, 3, 4, 3, 3, 2, '수업 템포가 빠르지만 교수님은 열정적이에요.'),
(3, 3, '2024-1', 5, 5, 5, 5, 5, 3, '추천합니다! 개념 정리가 정말 잘 되어 있어요.');

-- A-Rate 데모 데이터 삽입
-- 기존 테이블 구조에 맞춰 데모 데이터를 추가합니다.

USE arate_db;

-- 데모 사용자 삽입 (학생들)
INSERT INTO users (name, email, nickname, department, role, student_number, email_verified, provider) VALUES
('김지훈', 'jihun@ajou.ac.kr', '지훈이', '소프트웨어학과', 'STUDENT', '202012345', TRUE, 'GOOGLE'),
('박유진', 'yujin@ajou.ac.kr', '유진이', '컴퓨터공학과', 'STUDENT', '202012346', TRUE, 'GOOGLE'),
('이서연', 'seoyeon@ajou.ac.kr', '서연이', '정보통신대학', 'STUDENT', '202012347', TRUE, 'GOOGLE'),
('최민호', 'minho@ajou.ac.kr', '민호', '소프트웨어학과', 'STUDENT', '202012348', TRUE, 'GOOGLE'),
('정하늘', 'haneul@ajou.ac.kr', '하늘이', '컴퓨터공학과', 'STUDENT', '202012349', TRUE, 'GOOGLE');

-- 데모 교수 데이터는 이미 02-professors.sql에 있으므로 추가 강의평가만 삽입

-- 기존 강의에 대한 데모 강의평가 삽입
-- 강의 ID는 기존 03-lectures.sql에서 확인 후 사용
INSERT INTO lecture_evaluations (
    user_id, lecture_id, semester, content, 
    delivery_score, expertise_score, generosity_score, effectiveness_score, character_score, difficulty_score,
    assignment_amount, assignment_difficulty, exam, team_project
) VALUES
-- 오픈소스SW입문 강의에 대한 평가들
(1, 1, '2024-1', '설명이 친절하고 과제도 많지 않아서 좋았어요. 교수님이 학생들을 배려해주시는 게 느껴졌습니다.', 
 5, 4, 5, 4, 5, 3, 'FEW', 'NORMAL', 'BOTH', FALSE),

(2, 1, '2024-1', '전반적으로 무난하지만 조별과제가 좀 아쉬워요. 그래도 오픈소스에 대해 많이 배웠습니다.', 
 4, 4, 4, 4, 4, 2, 'NORMAL', 'EASY', 'BOTH', TRUE),

(3, 1, '2024-1', '처음 접하는 분야였는데 차근차근 설명해주셔서 이해하기 쉬웠어요!', 
 5, 5, 5, 5, 5, 2, 'FEW', 'EASY', 'FINAL', FALSE),

-- 데이터사이언스개론 강의에 대한 평가들  
(1, 2, '2024-1', '강의 내용이 아주 알차고 유익했습니다. 실습 위주로 진행되어서 좋았어요.', 
 5, 5, 4, 5, 4, 4, 'MANY', 'HARD', 'BOTH', TRUE),

(4, 2, '2024-1', '조금 어려웠지만 얻어가는 게 많았어요. 데이터 분석에 대한 기초를 탄탄히 다질 수 있었습니다.', 
 4, 5, 4, 5, 4, 4, 'MANY', 'NORMAL', 'BOTH', FALSE),

(5, 2, '2024-1', '과제가 많긴 하지만 그만큼 실력이 늘어나는 게 느껴져요. 추천합니다!', 
 4, 5, 3, 4, 4, 5, 'MANY', 'HARD', 'MIDTERM', TRUE),

-- 객체지향프로그래밍 강의에 대한 평가들
(2, 3, '2024-1', '수업 템포가 빠르지만 교수님은 열정적이에요. 프로그래밍 실력이 많이 늘었습니다.', 
 3, 4, 4, 3, 4, 3, 'NORMAL', 'NORMAL', 'BOTH', FALSE),

(3, 3, '2024-1', '추천합니다! 개념 정리가 정말 잘 되어 있어요. 객체지향의 핵심을 제대로 배웠습니다.', 
 5, 5, 5, 5, 5, 3, 'NORMAL', 'NORMAL', 'BOTH', TRUE),

(4, 3, '2024-1', '처음엔 어려웠지만 점점 재미있어졌어요. 코딩 실력이 확실히 늘었습니다.', 
 4, 4, 4, 4, 4, 3, 'FEW', 'NORMAL', 'FINAL', FALSE),

(5, 3, '2024-1', '과제를 통해 많이 배울 수 있었어요. 실무에서도 도움이 될 것 같습니다.', 
 4, 5, 4, 4, 5, 4, 'NORMAL', 'HARD', 'BOTH', TRUE);

-- 수강 인증 데모 데이터 삽입
INSERT INTO enrollments (student_id, lecture_id, grade, is_certified, semester, certified_at) VALUES
(1, 1, 'A+', TRUE, '2024-1', NOW()),
(2, 1, 'A0', TRUE, '2024-1', NOW()),
(3, 1, 'A+', TRUE, '2024-1', NOW()),
(1, 2, 'B+', TRUE, '2024-1', NOW()),
(4, 2, 'A0', TRUE, '2024-1', NOW()),
(5, 2, 'B0', TRUE, '2024-1', NOW()),
(2, 3, 'A0', TRUE, '2024-1', NOW()),
(3, 3, 'A+', TRUE, '2024-1', NOW()),
(4, 3, 'B+', TRUE, '2024-1', NOW()),
(5, 3, 'A0', TRUE, '2024-1', NOW());
