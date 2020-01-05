from django.db import models

class Livre(models.Model):
	titre = models.CharField(max_length = 300)
	auteur = models.CharField(max_length = 150, default = "Anonymous")
	contenu = models.TextField()


