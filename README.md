## Moteur de recherche d’une bibliotheque.

Dans ce projet, on appelle bibliotheque toute bases de données de taille
assez consequente de documents textuels. Un tel exemple est la base de Gutenberg disponible à l’adresse suivante : 
http://www.gutenberg.org/ ou les documents sont présentés sous différentes formes, dont le format textuel. A
l’instar de la base Gutenberg, une bibliotheque peut contenir des dizaines de milliers de documents, rendant une recherche
manuelle impossible. Un moteur de recherche dans une base de donnees est une application web/mobile permettant a
l’utilisateur d’acceder plus rapidement à un document textuel par recherche de mot-clef, ou par historique de recherche
en lien avec ce document (systeme de best-seller, de suggestion de publicite ciblée, de favoris utilisateur, et cetera). 

## Manuel

Le projet est séparé en 2 parties : la partie Python se trouve dans le dossier Bibliothèque, et la partie Java se trouve dans le projet BooksAPI.

Python


Installation de PostgreSQL

1. Installer PostgreSQL (la version 10 suffit) et le lancer.
2. Passer sur l'utilisateur 'postgres' avec :
```
sudo su -l postgres
```
3. Lancer un terminal interactif avec "psql"
4. Une fois dedans, changer le mot de passe de l'utilisateur postgres pour "daar" avec :
```
\password
```
Lancement de l'application Django

Dans le terminal, on fait
```bash
python manage.py makemigrations
```
et puis
```bash
python manage.py migrate
```

pour commencer, veuillez telecharger les livres dans la base de donnees d'abord :
```bash
python manage.py downloadbooks <nombre_livres>
```
et puis, il faut calculer la matrice de distance et les classement par :
```bash
python manage.py calcul_matrice_jaccard
```
pour lancer le server :
```bash
python manage.py runserver 
```
python
pour nettoyer les données dans model MatriceDistance et Classement:
```bash
python manage.py clean 
```

Java

1. Installer Tomcat 9.0
2. Déployer le WAR fourni sur Tomcat. L'intialisation peut être longue.
