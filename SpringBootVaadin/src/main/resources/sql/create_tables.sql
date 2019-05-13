DROP TABLE matrix;
DROP TABLE problem;

CREATE TABLE problem (
    id BIGINT AUTO_INCREMENT,
    description BLOB,
    matrix_dimension INT,
    parent_problem BIGINT,
    is_solved BIT(1),
    PRIMARY KEY (id),
    FOREIGN KEY (parent_problem) REFERENCES problem(id)--,
--     FOREIGN KEY (condition_id) REFERENCES matrix(problem_condition_id)
);

CREATE TABLE matrix (
    id BIGINT,
    i INT,
    j INT,
    float_value DOUBLE PRECISION,
    binary_value BLOB,
    problem_id BIGINT NOT NULL,
    is_condition BIT(1),
    PRIMARY KEY (id),
    FOREIGN KEY (problem_id) REFERENCES problem(id)
);