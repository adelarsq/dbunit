The DbUnit database testing framework
http://www.dbunit.org/
---

DbUnit is a JUnit extension (also usable from Ant) targeted 
for database-driven projects that, among other things, puts 
your database into a known state between test runs. This is 
an excellent way to avoid the myriad of problems that can 
occur when one test case corrupts the database and causes 
subsequent tests to fail or exacerbate the damage.

DbUnit has the ability to export and import your database 
(or specified tables) content to and from XML datasets. This 
is targeted for functional testing, so this is not the 
perfect tool to backup a huge production database.

DbUnit also provides assert facility to verify that your 
database content match some expected values.






