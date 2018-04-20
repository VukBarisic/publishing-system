# publishing-system
XML and Web Services class project - web application for scholarly papers reviewing and publishing.

## MarkLogic setup
1) Download [MarkLogic 8](https://developer.marklogic.com/products/marklogic-server/8.0).

2) Install as typical installation.

3) Start server from *Start menu -> Start MarkLogic Server*.  
And to stop the server later on use *Start menu -> Stop MarkLogic Server*.

4) Open http://localhost:8001  
*-> ok -> skip*.

5) Create the following admin account:  
`Admin: root`  
`Password: root`  
`Realm: public`  
and use it to log in.

6) Create an account for connecting to the database:  
*Security -> Users -> Create*  
`Username: root`  
`Password: root`  
And assign him the following roles:  
`admin, rest-admin, rest-reader, rest-writer`  
*-> ok*.

7) Create a new database:  
Open http://localhost:8000/appservices  
*Log in as admin -> New Database*  
`Database name: xmldb`

8) Create a database server:  
*Configure -> Check Collection Lexicon, REST API Instances, add New*  
`Server name: xmldbserver`  
`Port: 8011`

9) Enable RDF triple index option:  
Open http://localhost:8001  
*Databases -> xmldb -> triple index true -> ok*.

10) Check if database client works:  
*Run the Spring Boot application -> GET /api/db*.

## Team members
* [Aleksandar Nikolic](https://github.com/aleknik)
* [Helena Zecevic](https://github.com/helenazecevic)
* [Luka Maletin](https://github.com/lukamaletin)
