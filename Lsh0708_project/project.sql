CREATE SEQUENCE id_seq
START WITH 1
INCREMENT BY 1;

DROP SEQUENCE id_seq;

CREATE TABLE employee (
id NUMBER PRIMARY KEY,
name VARCHAR(100),
department VARCHAR(100),
birthdate VARCHAR(100),
address VARCHAR(100),
telNum VARCHAR(30),
sex VARCHAR(10)
);

insert into employee (id,name,department,birthdate,address,telNum,sex) values(id_seq,'lsh','연구소',
'1995-07-08','Busan','010-1234-4567','Male');

DELETE FROM employee WHERE id = 1;

select id_seq.nextval from dual;
select id_seq.currval from dual;


drop table employee;

select * from employee;

SELECT * FROM employee order by id desc;

commit;