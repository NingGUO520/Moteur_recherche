from django.db import models
import collections
import sys
import json

class LivreManager(models.Manager):
	

	def get_suggestions(self,id_target):
		cases = MatriceDistance.objects.all().filter(id_ligne=id_target).exclude(id_colonne=id_target)
		map_id_centralite = {}
		for case in cases:
			map_id_centralite[case.id_colonne] = case.value
		l = sorted(map_id_centralite.items(), key = lambda x: x[1])
		l = l[:3]
		result = []
		for x in l :
			i,value = x 
			result.append(i)
		return result

	def sort_by(self,listeLivres, methode):
		json_list = Classement.objects.all()[0].closeness
		jsonDec = json.decoder.JSONDecoder()
		closeness_list = jsonDec.decode(json_list)
		# print("closeness_list",closeness_list)

		if methode == "closeness":
			livres_id = [ i.id for i in listeLivres]
			# print("livres_id",livres_id)
			result = []
			for c in closeness_list:
				if int(c) in livres_id:
					result.append(c)
			print("result length",len(result))


			return result


	# # def sort_by_closeness(self,listeLivres):
		



	

class MatriceDistance(models.Model):
	id_ligne = models.IntegerField()
	id_colonne = models.IntegerField()
	value = models.DecimalField(decimal_places=3, max_digits = 10)

	def __str__(self):
		return "("+str(self.id_ligne)+","+str(self.id_colonne)+") = "+str(self.value)



class Livre(models.Model):
	titre = models.CharField(max_length = 300)
	auteur = models.CharField(max_length = 150, default = "Anonymous")
	language = models.CharField(max_length=50, default = 'English')
	contenu = models.TextField()

	objects = LivreManager()

	def __str__(self):
		return self.titre

	def snippet(self):
		return self.contenu[1500:1800]+"..."

class Classement(models.Model):
	closeness = models.TextField()
	betweenness = models.TextField()
	pagerank = models.TextField()






		