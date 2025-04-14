CREATE TABLE consultations (
    consultation_id INT PRIMARY KEY AUTO_INCREMENT,
    consultation_name VARCHAR(50) NOT NULL
);

CREATE TABLE questions (
    question_id INT PRIMARY KEY AUTO_INCREMENT,
    question_text VARCHAR(255) NOT NULL
);

CREATE TABLE answer_options (
    answer_option_id INT PRIMARY KEY AUTO_INCREMENT,
    question_id INT NOT NULL,
    answer_option VARCHAR(50) NOT NULL,
    deal_breaker BOOLEAN NOT NULL,
    FOREIGN KEY (question_id) REFERENCES questions(question_id)
);

CREATE TABLE consultation_questions (
    consultation_id INT NOT NULL,
    question_id INT NOT NULL,
    placement INT,
    PRIMARY KEY (consultation_id, question_id),
    FOREIGN KEY (consultation_id) REFERENCES consultations(consultation_id),
    FOREIGN KEY (question_id) REFERENCES questions(question_id)
);

CREATE TABLE consultation_status (
    status_id INT PRIMARY KEY AUTO_INCREMENT,
    status VARCHAR(50) NOT NULL
);

CREATE TABLE submissions (
    submission_id INT PRIMARY KEY AUTO_INCREMENT,
    consultation_id INT NOT NULL,
    reviewed BOOLEAN DEFAULT FALSE,
    review BOOLEAN,
    status_id INT DEFAULT 1,
    FOREIGN KEY (consultation_id) REFERENCES consultations(consultation_id),
    FOREIGN KEY (status_id) REFERENCES consultation_status(status_id)
);

CREATE TABLE submitted_answers (
    submission_id INT NOT NULL,
    question_id INT NOT NULL,
    answer_option_id INT NOT NULL,
    FOREIGN KEY (question_id) REFERENCES questions(question_id),
    FOREIGN KEY (answer_option_id) REFERENCES answer_options(answer_option_id)
);