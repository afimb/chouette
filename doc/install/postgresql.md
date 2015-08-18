# Create Postgres user and databases

Setup
-----

```sh
sudo su - postgres
createuser -d -P -U postgres chouette
Enter password for new role: chouette
Enter it again: chouette
psql -c "ALTER USER chouette SUPERUSER" 
createdb -O chouette iev
createdb -O chouette -E UTF-8 -T template1 chouette2
```

For installation from sources :

Create test and development databases
```sh
createdb -E UTF-8 -T template1 chouette_test
```

Note
----

If you're using chouette alone (without the [chouette2](https://github.com/afimb/chouette2) application), you will need to create a DB schema in the chouette2 DB for your referential (dataspace) before you can call the IEV API :

use [chouette.sql](./src/main/sql/chouette.sql) script : 

```sh
psql -h 127.0.0.1 -U chouette -v SCH=<schema_name>  -d chouette2 -f chouette.sql
```
where <schema_name> is your referential (dataspace) name (lowercase without space)
