INSERT INTO amrapari.class_info (id,class_name, description, instructor, schedule, capacity, enrolled, active, created_at) VALUES
(1,'Mathematics 101', 'Introduction to basic mathematics covering algebra, geometry, and trigonometry. Perfect for beginners.', 'Dr. Sarah Johnson', 'Monday, Wednesday, Friday 9:00 AM - 10:30 AM', 30, 25, true, CURRENT_TIMESTAMP),
(2,'Computer Science Fundamentals', 'Learn the basics of programming, data structures, and algorithms. No prior experience required.', 'Prof. Michael Chen', 'Tuesday, Thursday 2:00 PM - 4:00 PM', 25, 20, true, CURRENT_TIMESTAMP),
(3,'English Literature', 'Explore classic and modern literature from around the world. Develop critical reading and writing skills.', 'Dr. Emily Williams', 'Monday, Wednesday 11:00 AM - 12:30 PM', 35, 30, true, CURRENT_TIMESTAMP),
(4,'Physics for Engineers', 'Advanced physics concepts including mechanics, thermodynamics, and electromagnetism.', 'Prof. David Martinez', 'Tuesday, Thursday 10:00 AM - 12:00 PM', 20, 18, true, CURRENT_TIMESTAMP),
(5,'History of Art', 'Journey through art history from ancient civilizations to contemporary art movements.', 'Dr. Lisa Anderson', 'Friday 1:00 PM - 4:00 PM', 40, 35, true, CURRENT_TIMESTAMP);

insert into amrapari.home_slides values
(1, true, now(), 'Experience structured courses with interactive lessons, quizzes, and hands-on projects designed to make learning practical and engaging.', 35, 'https://plus.unsplash.com/premium_photo-1673292293042-cafd9c8a3ab3?q=80&w=687&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D', 'https://www.quantummethod.org', 'Interactive Learning Experience', now()),
(2, true, now(), 'Track your learning progress with smart dashboards, performance analytics, and personalized recommendations.', 35, 'https://plus.unsplash.com/premium_photo-1673292293042-cafd9c8a3ab3?q=80&w=687&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D', 'https://www.quantummethod.org', 'Smart Progress Tracking', now()),
(3, true, now(), 'Learn from experienced instructors and industry professionals who guide you through real-world skills and concepts.', 35, 'https://plus.unsplash.com/premium_photo-1673292293042-cafd9c8a3ab3?q=80&w=687&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D', 'https://www.quantummethod.org', 'Learn from Expert Instructors', now()),
(4, true, now(), 'Build your career with practical courses, certifications, and skill-based learning paths tailored for modern education.', 35, 'https://plus.unsplash.com/premium_photo-1673292293042-cafd9c8a3ab3?q=80&w=687&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D', 'https://www.quantummethod.org', 'Career-Focused Learning', now()),
(5, true, now(), 'Access your courses anytime from any device and continue learning at your own pace without limitations.', 35, 'https://plus.unsplash.com/premium_photo-1673292293042-cafd9c8a3ab3?q=80&w=687&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D', 'https://www.quantummethod.org', 'Flexible Learning for Everyone', now());

insert into amrapari."user" values
                                (9,null,null,null,null,'test1@gmail.com', '$2a$12$ypz8FTxASP37dCmE5K2Yn..KOlJJx5KxW87NEGcPFR1HY6Psm/Ub2', null,null,'test1'),
                                (10,null,null,null,null,'test2@gmail.com', '$2a$12$ypz8FTxASP37dCmE5K2Yn..KOlJJx5KxW87NEGcPFR1HY6Psm/Ub2', null,null,'test2'),
                                (11,null,null,null,null,'test3@gmail.com', '$2a$12$ypz8FTxASP37dCmE5K2Yn..KOlJJx5KxW87NEGcPFR1HY6Psm/Ub2', null,null,'test3'),
                                (12,null,null,null,null,'test4@gmail.com', '$2a$12$ypz8FTxASP37dCmE5K2Yn..KOlJJx5KxW87NEGcPFR1HY6Psm/Ub2', null,null,'test4'),
                                (13,null,null,null,null,'test5@gmail.com', '$2a$12$ypz8FTxASP37dCmE5K2Yn..KOlJJx5KxW87NEGcPFR1HY6Psm/Ub2', null,null,'test5'),
                                (14,null,null,null,null,'test6@gmail.com', '$2a$12$ypz8FTxASP37dCmE5K2Yn..KOlJJx5KxW87NEGcPFR1HY6Psm/Ub2', null,null,'test6'),
                                (15,null,null,null,null,'test7@gmail.com', '$2a$12$ypz8FTxASP37dCmE5K2Yn..KOlJJx5KxW87NEGcPFR1HY6Psm/Ub2', null,null,'test7'),
                                (16,null,null,null,null,'test8@gmail.com', '$2a$12$ypz8FTxASP37dCmE5K2Yn..KOlJJx5KxW87NEGcPFR1HY6Psm/Ub2', null,null,'test8'),
                                (17,null,null,null,null,'test9@gmail.com', '$2a$12$ypz8FTxASP37dCmE5K2Yn..KOlJJx5KxW87NEGcPFR1HY6Psm/Ub2', null,null,'test9'),
                                (18,null,null,null,null,'test10@gmail.com', '$2a$12$ypz8FTxASP37dCmE5K2Yn..KOlJJx5KxW87NEGcPFR1HY6Psm/Ub2', null,null,'test10'),
                                (19,null,null,null,null,'test11@gmail.com', '$2a$12$ypz8FTxASP37dCmE5K2Yn..KOlJJx5KxW87NEGcPFR1HY6Psm/Ub2', null,null,'test11'),
                                (20,null,null,null,null,'test12@gmail.com', '$2a$12$ypz8FTxASP37dCmE5K2Yn..KOlJJx5KxW87NEGcPFR1HY6Psm/Ub2', null,null,'test12'),
                                (21,null,null,null,null,'test13@gmail.com', '$2a$12$ypz8FTxASP37dCmE5K2Yn..KOlJJx5KxW87NEGcPFR1HY6Psm/Ub2', null,null,'test13'),
                                (22,null,null,null,null,'test14@gmail.com', '$2a$12$ypz8FTxASP37dCmE5K2Yn..KOlJJx5KxW87NEGcPFR1HY6Psm/Ub2', null,null,'test14'),
                                (23,null,null,null,null,'test15@gmail.com', '$2a$12$ypz8FTxASP37dCmE5K2Yn..KOlJJx5KxW87NEGcPFR1HY6Psm/Ub2', null,null,'test15'),
                                (24,null,null,null,null,'test16@gmail.com', '$2a$12$ypz8FTxASP37dCmE5K2Yn..KOlJJx5KxW87NEGcPFR1HY6Psm/Ub2', null,null,'test16'),
                                (25,null,null,null,null,'test17@gmail.com', '$2a$12$ypz8FTxASP37dCmE5K2Yn..KOlJJx5KxW87NEGcPFR1HY6Psm/Ub2', null,null,'test17'),
                                (26,null,null,null,null,'test18@gmail.com', '$2a$12$ypz8FTxASP37dCmE5K2Yn..KOlJJx5KxW87NEGcPFR1HY6Psm/Ub2', null,null,'test18'),
                                (27,null,null,null,null,'test19@gmail.com', '$2a$12$ypz8FTxASP37dCmE5K2Yn..KOlJJx5KxW87NEGcPFR1HY6Psm/Ub2', null,null,'test19'),
                                (28,null,null,null,null,'test20@gmail.com', '$2a$12$ypz8FTxASP37dCmE5K2Yn..KOlJJx5KxW87NEGcPFR1HY6Psm/Ub2', null,null,'test20');


-- ============================================================
-- Dummy Users Data — 30 records for PostgreSQL
-- Table: user (matches your User.java entity)
-- ============================================================

INSERT INTO amrapari."user" (email, username, password, reset_token, reset_token_expiry, created_at, updated_at, created_by, updated_by)
VALUES
    -- password for all users is: Password@123
    -- BCrypt hash of "Password@123" with strength 12
    ('alice.johnson@mail.com',   'alice_johnson',   '$2a$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', NULL, NULL, NOW(), NOW(), 0, 0),
    ('bob.smith@mail.com',       'bob_smith',       '$2a$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', NULL, NULL, NOW() - INTERVAL '1 day',  NOW() - INTERVAL '1 day',  0, 0),
    ('carol.white@mail.com',     'carol_white',     '$2a$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', NULL, NULL, NOW() - INTERVAL '2 days', NOW() - INTERVAL '2 days', 0, 0),
    ('david.brown@mail.com',     'david_brown',     '$2a$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', NULL, NULL, NOW() - INTERVAL '3 days', NOW() - INTERVAL '3 days', 0, 0),
    ('eva.martinez@mail.com',    'eva_martinez',    '$2a$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', NULL, NULL, NOW() - INTERVAL '4 days', NOW() - INTERVAL '4 days', 0, 0),
    ('frank.wilson@mail.com',    'frank_wilson',    '$2a$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', NULL, NULL, NOW() - INTERVAL '5 days', NOW() - INTERVAL '5 days', 0, 0),
    ('grace.taylor@mail.com',    'grace_taylor',    '$2a$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', NULL, NULL, NOW() - INTERVAL '6 days', NOW() - INTERVAL '6 days', 0, 0),
    ('henry.anderson@mail.com',  'henry_anderson',  '$2a$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', NULL, NULL, NOW() - INTERVAL '7 days', NOW() - INTERVAL '7 days', 0, 0),
    ('iris.thomas@mail.com',     'iris_thomas',     '$2a$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', NULL, NULL, NOW() - INTERVAL '8 days', NOW() - INTERVAL '8 days', 0, 0),
    ('jack.jackson@mail.com',    'jack_jackson',    '$2a$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', NULL, NULL, NOW() - INTERVAL '9 days', NOW() - INTERVAL '9 days', 0, 0),

    ('karen.harris@mail.com',    'karen_harris',    '$2a$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', NULL, NULL, NOW() - INTERVAL '10 days', NOW() - INTERVAL '10 days', 0, 0),
    ('liam.martin@mail.com',     'liam_martin',     '$2a$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', NULL, NULL, NOW() - INTERVAL '11 days', NOW() - INTERVAL '11 days', 0, 0),
    ('mia.garcia@mail.com',      'mia_garcia',      '$2a$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', NULL, NULL, NOW() - INTERVAL '12 days', NOW() - INTERVAL '12 days', 0, 0),
    ('noah.davis@mail.com',      'noah_davis',      '$2a$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', NULL, NULL, NOW() - INTERVAL '13 days', NOW() - INTERVAL '13 days', 0, 0),
    ('olivia.rodriguez@mail.com','olivia_rodriguez', '$2a$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', NULL, NULL, NOW() - INTERVAL '14 days', NOW() - INTERVAL '14 days', 0, 0),
    ('peter.lewis@mail.com',     'peter_lewis',     '$2a$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', NULL, NULL, NOW() - INTERVAL '15 days', NOW() - INTERVAL '15 days', 0, 0),
    ('quinn.lee@mail.com',       'quinn_lee',       '$2a$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', NULL, NULL, NOW() - INTERVAL '16 days', NOW() - INTERVAL '16 days', 0, 0),
    ('rachel.walker@mail.com',   'rachel_walker',   '$2a$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', NULL, NULL, NOW() - INTERVAL '17 days', NOW() - INTERVAL '17 days', 0, 0),
    ('sam.hall@mail.com',        'sam_hall',        '$2a$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', NULL, NULL, NOW() - INTERVAL '18 days', NOW() - INTERVAL '18 days', 0, 0),
    ('tina.allen@mail.com',      'tina_allen',      '$2a$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', NULL, NULL, NOW() - INTERVAL '19 days', NOW() - INTERVAL '19 days', 0, 0),

    ('uma.young@mail.com',       'uma_young',       '$2a$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', NULL, NULL, NOW() - INTERVAL '20 days', NOW() - INTERVAL '20 days', 0, 0),
    ('victor.king@mail.com',     'victor_king',     '$2a$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', NULL, NULL, NOW() - INTERVAL '21 days', NOW() - INTERVAL '21 days', 0, 0),
    ('wendy.wright@mail.com',    'wendy_wright',    '$2a$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', NULL, NULL, NOW() - INTERVAL '22 days', NOW() - INTERVAL '22 days', 0, 0),
    ('xander.scott@mail.com',    'xander_scott',    '$2a$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', NULL, NULL, NOW() - INTERVAL '23 days', NOW() - INTERVAL '23 days', 0, 0),
    ('yara.green@mail.com',      'yara_green',      '$2a$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', NULL, NULL, NOW() - INTERVAL '24 days', NOW() - INTERVAL '24 days', 0, 0),
    ('zack.baker@mail.com',      'zack_baker',      '$2a$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', NULL, NULL, NOW() - INTERVAL '25 days', NOW() - INTERVAL '25 days', 0, 0),
    ('amy.adams@mail.com',       'amy_adams',       '$2a$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', NULL, NULL, NOW() - INTERVAL '26 days', NOW() - INTERVAL '26 days', 0, 0),
    ('brian.nelson@mail.com',    'brian_nelson',    '$2a$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', NULL, NULL, NOW() - INTERVAL '27 days', NOW() - INTERVAL '27 days', 0, 0),
    ('claire.carter@mail.com',   'claire_carter',   '$2a$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', NULL, NULL, NOW() - INTERVAL '28 days', NOW() - INTERVAL '28 days', 0, 0),
    ('daniel.mitchell@mail.com', 'daniel_mitchell', '$2a$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', NULL, NULL, NOW() - INTERVAL '29 days', NOW() - INTERVAL '29 days', 0, 0);

