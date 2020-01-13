from django.db import models
import collections
import sys

class LivreManager(models.Manager):
	def __init(self):
		self.n = self.count()
		self.matrice_dis = calcul_distance()

	def calcul_distance(self):
		matrice_dis = [[sys.maxsize for _ in range(n+1)] for _ in range(n+1)]
		map_id_dic = {}
		for livre in self.all():
			map_id_dic[livre.id] = get_dic(livre.contenu)

		for i in range(1,range(n+1)):
			for j in range(1,range(n+1)):
				if i == j :
					matrice_dis[i][j] = 0 
				else :
					matrice_dis[i][j] = dis_jaccard(map_id_dic[i],map_id_dic[j])
		return matrice_dis



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





		