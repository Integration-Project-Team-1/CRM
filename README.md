# Salesforce CRM Integratie Project

Welkom bij het repository voor ons integratieproject waarbij we Salesforce en RabbitMQ gebruiken voor het beheren van een evenement! Dit project heeft tot doel om de functionaliteiten van Salesforce te integreren met RabbitMQ voor het efficiënt beheren van de inschrijvingen, communicatie en andere processen met betrekking tot het evenement.

## Vereisten
- Salesforce Developer Account
- Basiskennis van Salesforce API's en authenticatiemethoden
- Vertrouwdheid met RabbitMQ voor berichtenwachtrijen
- Java JDK geïnstalleerd op jouw ontwikkelomgeving

## Aan de slag
Om te beginnen met het integreren van Salesforce en RabbitMQ in jouw Java-applicatie voor het school evenement, volg deze stappen:
1. Kloon dit repository lokaal.
2. Configureer jouw Salesforce inloggegevens en RabbitMQ-verbinding in het `.env`-bestand.
3. Importeer het project in jouw favoriete Java-ontwikkelomgeving.
4. Voeg de benodigde Salesforce- en RabbitMQ-bibliotheken toe aan jouw project.
5. Implementeer de functionaliteiten voor inschrijvingen, communicatie en andere processen met Salesforce en RabbitMQ.

## Configuratie
Zorg ervoor dat de volgende omgevingsvariabelen zijn ingesteld in jouw `.env`-bestand:
- `SALESFORCE_CLIENT_ID`: Jouw Salesforce OAuth-client-ID.
- `SALESFORCE_CLIENT_SECRET`: Jouw Salesforce OAuth-clientgeheim.
- `SALESFORCE_USERNAME`: Jouw Salesforce gebruikersnaam.
- `SALESFORCE_PASSWORD`: Jouw Salesforce wachtwoord.
- `SALESFORCE_SECURITY_TOKEN`: Jouw Salesforce beveiligingstoken.
- `RABBITMQ_HOST`: Hostnaam of IP-adres van jouw RabbitMQ-server.
- `RABBITMQ_PORT`: Poortnummer van jouw RabbitMQ-server.
- `RABBITMQ_USERNAME`: Gebruikersnaam voor RabbitMQ-authenticatie.
- `RABBITMQ_PASSWORD`: Wachtwoord voor RabbitMQ-authenticatie.

## Gebruik
Eenmaal geconfigureerd, kun je integreren met zowel Salesforce- als RabbitMQ-functionaliteiten binnen jouw Java-applicatie voor het school evenement. Maak gebruik van Salesforce API's voor het beheren van gegevens en RabbitMQ voor het verwerken van berichten en communicatie.

## Problemen oplossen
Als je problemen ondervindt tijdens de integratie, overweeg dan de volgende stappen voor probleemoplossing:
- Controleer jouw Salesforce- en RabbitMQ-configuraties en zorg ervoor dat ze correct zijn.
- Verifieer de netwerkconnectiviteit en toegangsrechten voor zowel Salesforce als RabbitMQ.
- Controleer RabbitMQ-queues en -uitwisselingen voor berichtenverwerking.

## Referenties
- [Salesforce Developer-documentatie](https://developer.salesforce.com/docs)
- [Salesforce REST API Developer Guide](https://developer.salesforce.com/docs/atlas.en-us.api_rest/)
- [RabbitMQ Documentation](https://www.rabbitmq.com/documentation.html)



## Bijdragers
- Bouzourti Ismael - Projectleider 
- Hamoumi Soufiane - Ontwikkelaar
- Amghar Hamza - Ontwikkelaar 
