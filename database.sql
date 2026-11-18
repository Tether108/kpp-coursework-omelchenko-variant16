-- База даних для курсової роботи: облік дефектів будівельних об'єктів (варіант 16)

SET NAMES utf8mb4;

DROP DATABASE IF EXISTS building_defects;
CREATE DATABASE building_defects CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE building_defects;

CREATE TABLE building_object (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    address VARCHAR(200) NOT NULL,
    object_type VARCHAR(100) NOT NULL,
    build_year INT NOT NULL
);

CREATE TABLE inspection (
    id INT AUTO_INCREMENT PRIMARY KEY,
    object_id INT NOT NULL,
    inspection_date DATE NOT NULL,
    inspector VARCHAR(100) NOT NULL,
    condition_category INT NOT NULL,
    notes VARCHAR(255),
    CONSTRAINT fk_inspection_object FOREIGN KEY (object_id) REFERENCES building_object (id)
);

CREATE TABLE defect (
    id INT AUTO_INCREMENT PRIMARY KEY,
    inspection_id INT NOT NULL,
    description VARCHAR(255) NOT NULL,
    severity INT NOT NULL,
    area DOUBLE NOT NULL,
    safety_score INT NOT NULL,
    state VARCHAR(30) NOT NULL DEFAULT 'REGISTERED',
    f1 DOUBLE NOT NULL,
    f2 DOUBLE NOT NULL,
    f3 DOUBLE NOT NULL,
    risk_index DOUBLE NOT NULL,
    CONSTRAINT fk_defect_inspection FOREIGN KEY (inspection_id) REFERENCES inspection (id)
);

CREATE TABLE repair_task (
    id INT AUTO_INCREMENT PRIMARY KEY,
    defect_id INT NOT NULL,
    description VARCHAR(255) NOT NULL,
    executor VARCHAR(100) NOT NULL,
    planned_date DATE NOT NULL,
    CONSTRAINT fk_task_defect FOREIGN KEY (defect_id) REFERENCES defect (id)
);

INSERT INTO building_object (name, address, object_type, build_year) VALUES
('Навчальний корпус №2', 'м. Полтава, вул. Садова, 14', 'Громадська будівля', 1972),
('Житловий будинок', 'м. Полтава, вул. Миру, 51', 'Житловий будинок', 1964),
('Гуртожиток №1', 'м. Полтава, вул. Козацька, 7', 'Гуртожиток', 1980);


INSERT INTO inspection (object_id, inspection_date, inspector, condition_category, notes) VALUES
(1, '2026-09-15', 'Коваленко О.М.', 3, 'Тріщини у стінах, протікання покрівлі'),
(2, '2026-09-22', 'Мельник В.П.', 4, 'Балконні плити в аварійному стані'),
(2, '2026-10-05', 'Коваленко О.М.', 2, 'Пошкоджене оздоблення фасаду'),
(3, '2026-10-12', 'Бондар І.С.', 3, 'Корозія в підвальному приміщенні');

-- f1, f2, f3 і risk_index пораховані так само, як у програмі (IndexCalculator)
INSERT INTO defect (inspection_id, description, severity, area, safety_score, state, f1, f2, f3, risk_index) VALUES
(1, 'Тріщина в несучій стіні другого поверху', 4, 3.5, 80, 'REPAIR_ASSIGNED', 75, 14, 80, 54.9),
(1, 'Протікання покрівлі над сходовою кліткою', 3, 12, 40, 'CHECKED', 50, 48, 40, 46.8),
(2, 'Руйнування захисного шару бетону балконної плити', 5, 2, 95, 'REGISTERED', 100, 8, 95, 66.55),
(3, 'Відшарування штукатурки фасаду', 2, 30, 25, 'CLOSED', 25, 100, 25, 51.25),
(4, 'Корозія арматури колони підвалу', 4, 1.5, 70, 'REGISTERED', 75, 6, 70, 49.6);

INSERT INTO repair_task (defect_id, description, executor, planned_date) VALUES
(1, 'Ін''єктування тріщини та встановлення маяків', 'ТОВ Будсервіс', '2026-11-25'),
(4, 'Відновлення штукатурного шару фасаду', 'КП Житлосервіс', '2026-10-30');
