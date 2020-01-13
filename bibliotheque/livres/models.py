from django.db import models
import collections

class LivreManager(models.Manager):
	def calcul_distance(self):
		matrice_dis = {}
		map_id_dic = {}
		for livre in self.all():
			map_id_dic[livre.id] = get_dic(livre.contenu)
		return map_id_dic



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


# fonction qui permet de calculer la distance de Jaccard entre deux documents	  
# dic1 le premier document (livre)
# dic2 le deuxième document (livre)
def dis_jaccard(dic1, dic2):
	somme1 = 0
	somme2 = 0 
	for mot in dic1 :
		if not mot in dic2:
			somme1 += dic1[mot]
			somme2 += dic1[mot]
		else :
			somme1 += max(dic2[mot],dic1[mot]) - min(dic2[mot],dic1[mot]) 
			somme2 += max(dic1[mot],dic2[mot])


	for mot in dic2 :
		if not mot in dic1:
			somme1 += dic2[mot]
			somme2 += dic2[mot]
	return somme1/somme2

def get_dic(txt):
	return collections.Counter(txt.replace(',',' ').replace('.',' ').split())

def calcul_distance():
	matrice_dis = {}
	map_id_dic = {}
	for livre in Livre.objects.all():
		map_id_dic[livre.id] = get_dic(livre.contenu)
	return map_id_dic
		




		