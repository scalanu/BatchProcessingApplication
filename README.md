This project used Spring batch to achieve ETL tasks on bulk. Reads from database to flat file csv and then reads from flat file back to database. 

Both jobs in a single application that runs one after another.
If you intend to run one job only pass 'ImportFile' or 'ExportFile' as args to the application 
eg. "java -jar target/{jarName}.jar ImportFile"

Used stack
 . Zulu Jdk 11
 . H2 database
 . Spring Boot 2.7.10
 . Spring Batch 2.7.10
 . Spring Batch Test 4.3.8


