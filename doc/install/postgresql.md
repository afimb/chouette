# Install Postgresql

Setup
-----

```sh
sudo su - postgres
createuser -d -P -U postgres chouette
Enter password for new role: chouette
Enter it again: chouette
psql -c "ALTER USER chouette SUPERUSER" 
```sh
createdb -O chouette -U postgres iev
```

