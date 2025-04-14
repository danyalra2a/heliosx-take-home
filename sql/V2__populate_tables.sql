INSERT INTO consultations (consultation_name)
VALUES ('pearfect-cure');

INSERT INTO questions (question_text)
VALUES
('Are you aged between 18 and 75?'),
('Are you allergic to x medication?'),
('What would you rate Genovia pears from 1-3?');

INSERT INTO answer_options (question_id, answer_option, deal_breaker)
SELECT q.question_id, ao.answer_option, ao.deal_breaker
FROM (
    SELECT 'Are you aged between 18 and 75?' AS question_text, 'yes' AS answer_option, FALSE AS deal_breaker UNION ALL
    SELECT 'Are you aged between 18 and 75?', 'no', TRUE UNION ALL
    SELECT 'Are you allergic to x medication?', 'yes', TRUE UNION ALL
    SELECT 'Are you allergic to x medication?', 'no', FALSE UNION ALL
    SELECT 'What would you rate Genovia pears from 1-3?', '1', FALSE UNION ALL
    SELECT 'What would you rate Genovia pears from 1-3?', '2', FALSE UNION ALL
    SELECT 'What would you rate Genovia pears from 1-3?', '3', FALSE
) ao
JOIN questions q ON q.question_text = ao.question_text;

INSERT INTO consultation_questions (consultation_id, question_id, placement)
SELECT c.consultation_id, q.question_id, cq.placement
FROM (
    SELECT 'Are you aged between 18 and 75?' AS question_text, 1 AS placement UNION ALL
    SELECT 'Are you allergic to x medication?', 2 UNION ALL
    SELECT 'What would you rate Genovia pears from 1-3?', 3
) cq
JOIN questions q ON q.question_text = cq.question_text
JOIN consultations c ON c.consultation_name = 'pearfect-cure';

INSERT INTO consultation_status (status)
VALUES
('Unprocessed'),
('Rejected'),
('Awaiting Review'),
('Approved');