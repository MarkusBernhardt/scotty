delete from reconciliation;
delete from payment;
MERGE INTO payment SELECT * FROM CSVREAD('src\test\manual\payment.csv', null, 'charset=UTF-8');
MERGE INTO reconciliation SELECT * FROM CSVREAD('src\test\manual\reconciliation.csv', null, 'charset=UTF-8');
SELECT count(*) from payment;
SELECT count(*) from reconciliation;
