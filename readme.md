# Reservio
Både frontend og backend er deployet, så for å teste applikasjonen så kan man simpelthen gå til http://13.51.58.86:5000

Hvis man ønsker å kjøre backend eller frontend lokalt så er det beskrevet hvordan man gjør dette under:

Hvis man vil kjøre backend lokalt i tillegg til frontend så er man nødt til å endre på serverUrl-variabelen i
``gui/src/main.js`` fra 13.51.58.86 til localhost
## Backend
### Databastilgang
Hvis man ønsker å kjøre backend lokalt så er man avhengig av egen database, inloggingsinformasjonen til denne må man legge i en application.properties-fil. Den filen skal ligge her i mappestrukturen til prosjektet:
```
reserved/src/main/resources/
```
Backend kjører på HTTPS så man er også avhengig av å generere eget sertifikat (med mindre man er villig til å betale for
slikt da)

Sertifikatet må plasseres slik:
```
reserved/src/main/resources/keypair/reservio.p12
```
Hvis man ønsker å endre navn på filen så er man nødt til å endre det i application.properties under også.

Det er en del ekstra informasjon som er inkludert her som ikke er strengt tatt nødvendig inkludere for funksjonaliteten
men er inkludert fordi det ble brukt av oss under utvikling.

Det eneste man trenger å sette in for databasetilgang er de tre første verdiene

Siden backend kjører på HTTPS så må man også sette inn alias og passord til egen nøkkelfil (og eget filnavn om man 
ønsker å endre fra reservio.p12)

Man kan generere egen nøkkelfil med følgende kommando:
```
keytool -genkeypair -alias *sett inn alias her* -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore reservio.p12 -validity 3650 -storepass *sett in passord her*
```

Vår application.properties-fil ser slik ut:
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
server.ssl.key-store-password=*sett in passord her*
server.ssl.key-alias=*sett inn alias her*
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
Backend vil da kjøre og lytte på port 8080 (kanskje 8081 hvis 8080 var opptatt) og 8443

Man kan alternativt kjøre den vedlagte dockerfilen, denne ligger i roten av prosjektet

Dockerfilen kan bygges ved hjelp av kommandoen:
```
sudo docker build -f runfrontend -t frontend .
```
Og så kjøres ved hjelp av kommandoen:
```
sudo docker run backend
```

Backend vil da lytte på port 8080 (kanskje 8081 hvis 8080 var opptatt) og 8443 (mest sannsynlig, men man kan sjekke output i programmet om noe virker feil)

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
på http://localhost:8080 men porten kan endre seg hvis 8080 er opptatt når man starter applikasjonen
