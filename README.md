# BUT-S4-Projet-Application-Repartie
BUT-S4-Projet-Application-Repartie
Groupe 3 (RA-IL 1)
Martin GOUTHIER
Enzo POIRSON
Yanis HUSSER
Léo BOUGNOUX


## Installation/Lancement des services:

### Front (TS)
```npm run build``` dans le dossier front/
### Back (ServiceRMI)
```javac -cp .:lib/gson-2.11.0.jar *.java``` dans le dossier back
```java -cp .:lib/* LancerService``` dans le dossier back
### Proxy (HttpClient)
```javac ProxyServer.java```dans le dossier proxy
```java ProxyServer.java```dans le dossier proxy
