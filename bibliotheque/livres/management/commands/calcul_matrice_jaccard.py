from django.core.management.base import BaseCommand
from django.utils import timezone
from livres.models import Livre, MatriceDistance
import collections
import requests



class Command(BaseCommand):
	help = 'calculer la matrice de distance Jaccard entre les livres'

	
	def handle(self,*args,**kwargs):
		list_id = Livre.objects.values_list('id',flat=True) #flat True indique il returne seulement un value

		self.stdout.write('Calculating the matrix Jaccard ... ')

		matrice_dis = calcul_distance(list_id)
		for k,value in matrice_dis.items():
			id1,id2 = k 
			case = MatriceDistance()
			case.id_ligne = id1
			case.id_colonne = id2 
			case.value = value 
			case.save()
		self.stdout.write('The matrix Jaccard has been caculated successfully')

def calcul_plus_court(list_id,matrix):



def calcul_distance(list_id):
	matrice_dis = {}
	map_id_dic = {}
	for livre in Livre.objects.all():
		map_id_dic[livre.id] = get_dic(livre.contenu)

	for id1 in list_id:
		for id2 in list_id:
			if id1 == id2 :
				matrice_dis[(id1,id2)] = 0 
			else :
				if (id2,id1) in matrice_dis.keys():
					matrice_dis[(id1,id2)] = matrice_dis[(id2,id1)]
				else :
					matrice_dis[(id1,id2)] = dis_jaccard(map_id_dic[id1],map_id_dic[id2])
	return matrice_dis

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
