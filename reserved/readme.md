# Reservio
Vi har lagt opp prosjektet slik at vi har deployet backend til en AWS server, ip-adressen
til denne serveren er konfigurert i frontend, så man trenger ikke å endre på noe som helst der. 
Frontend må derimot kjøres lokalt. Det er også mulig å kjøre backend lokalt om ønskelig.

Hvis man vil kjøre backend lokalt i tillegg så er man nødt til å endre på serverUrl-variabelen i
``gui/src/main.js`` fra 13.51.58.86 til localhost
## Backend
### Databastilgang
Hvis man ønsker å kjøre backend lokalt så er man avhengig av egen database, inloggingsinformasjonen til denne må man legge i en application.properties-fil. Den filen skal ligge her i mappestrukturen til prosjektet:
```
reserved/src/main/resources/
```
Vår application.properties-fil ser slik ut:

Det er en del ekstra informasjon som er inkludert her som ikke er strengt tatt nødvendig inkludere for funksjonaliteten
men er inkludert fordi det ble brukt av oss under utvikling.

Det eneste man trenger å sette in for å kjøre backend lokalt er de tre første verdiene
```
spring.datasource.url=*sett in link til egen databasetabell her, inkluder navnet til databasen*
spring.datasource.username=*sett inn brukernavn til databaseinloggingen*
spring.datasource.password=*sett inn passordet til databaseinloggingen*
spring.datasource.driver-class-name=com.mysql.jdbc.Driver
# Keep the connection alive if idle for a long time (needed in production)
spring.datasource.testWhileIdle = true
spring.datasource.validationQuery = SELECT 1
spring.jpa.show-sql = true
spring.jpa.hibernate.ddl-auto = update
spring.jpa.hibernate.naming-strategy = org.hibernate.cfg.ImprovedNamingStrategy
spring.jpa.properties.hibernate.dialect = org.hibernate.dialect.MySQL5Dialect
server.ssl.key-store-type=PKCS12
server.ssl.key-store=src/main/resources/keypair/reservio.p12
server.ssl.key-store-password=reservedpassword
server.ssl.key-alias=reservio
server.ssl.enabled=true
server.ssl.key.alias=ode-https
server.ssl.protocol=TLS
server.http.port=8080
server.port=8443

secretKey = ZXJsaW5zc2x4aWtob3ZpbmQ9dHJ1ZTwz
server.error.include-message = always
server.error.include-binding-errors = always
```

SecretKey blir brukt til å generere tokens, naturligvis så burde vi ikke ha lagt den ut for sikkerheten sin skyld, men for at prosjektet skal være lettest mulig å klone så har vi valgt å dele den likevel. 

### Kjøring av backend
Letteste måten å kjøre backend er å kjøre via maven ved hjelp av kommandoen:

Denne må kjøres i reservation-mappen
```
mvn spring-boot:run
```
Backend vil da kjøre og lytte på port 8080 og 8443

Man kan alternativt kjøre den vedlagte dockerfilen, denne ligger i roten av prosjektet

Dockerfilen kan bygges ved hjelp av kommandoen:
```
sudo docker build -f runfrontend -t frontend .
```
Og så kjøres ved hjelp av kommandoen:
```
sudo docker run backend
```

Backend vil da lytte på port 8080 og 8443

## Frontend
Frontend kan kjøres ved hjelp av npm eller ved hjelp av den vedlagte dockerfilen

### Kjøring vha. npm
Kjør først:
```
npm install
```
For å installere nødvendige avhengigheter

Kjør så
```
npm run serve
```
For å kjøre frontend lokalt.


### Kjøring ved hjelp av docker
Dockerfilen finnes i roten av prosjektet

Dockerfilen for frontend kan bygges slik:
```
sudo docker build -f runfrontend -t frontend .
```
Den kan så kjøres slik:
```
sudo docker run frontend
```
I output av prosessen så vil man se hvilken port applikasjonen kjører på, mest sannsynlig så vil man ha tilgang på den 
på http://localhost:8080
