DROP TABLE IF EXISTS Customers;
  
CREATE TABLE Customers (
 id bigint AUTO_INCREMENT  PRIMARY KEY,
companyinfo varchar(200),
created datetime(6) NULL, 
email varchar(254) NULL, 
emailprofile varchar(200) NULL, 
employeecount int NULL, 
initiatives varchar(200) NULL, 
localprofile varchar(200) NULL, 
modified datetime(6) NULL, 
optin int NULL, 
storecustomerid int NULL
);


 
