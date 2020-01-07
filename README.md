## Moteur de recherche d’une bibliotheque.

Dans ce projet, on appelle bibliotheque toute base de données de taille
assez consequente de documents textuels. Un tel exemple est la base de Gutenberg disponible à l’adresse suivante : 
http://www.gutenberg.org/ ou les documents sont présentés sous différentes formes, dont le format textuel. A
l’instar de la base Gutenberg, une bibliotheque peut contenir des dizaines de milliers de documents, rendant une recherche
manuelle impossible. Un moteur de recherche dans une base de donnees est une application web/mobile permettant a
l’utilisateur d’acceder plus rapidement à un document textuel par recherche de mot-clef, ou par historique de recherche
en lien avec ce document (systeme de best-seller, de suggestion de publicite ciblée, de favoris utilisateur, et cetera). 

## Manuel
Tout d'abord, il faut lancer le server postgresSQL sur une database 'postgres'

pour commencer, veuillez telecharger les livres dans la base de donnees d'abord :
```bash
python manage.py downloadbooks <nombre_livres>
```

et puis, pour lancer le server :
```bash
python manage.py runserver 
```

