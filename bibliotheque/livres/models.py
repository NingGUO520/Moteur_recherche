from django.db import models

class Livre(models.Model):
	titre = models.CharField(max_length = 300)
	auteur = models.CharField(max_length = 150, default = "Anonymous")
	language = models.CharField(max_length=50, default = 'English')
	contenu = models.TextField()


