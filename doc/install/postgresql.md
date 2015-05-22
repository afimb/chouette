# Install Postgresql

Setup
-----

```sh
sudo su - postgres
createuser -d -P -U postgres chouette
Enter password for new role: chouette
Enter it again: chouette
psql -c "ALTER USER chouette SUPERUSER" 
createdb -O chouette -U postgres iev
```

if chouette2 is not used :

```sh
createdb -O chouette -E UTF-8 -T template1 chouette2
```

