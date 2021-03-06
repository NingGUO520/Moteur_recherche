from django.core.management.base import BaseCommand
from django.utils import timezone
from livres.models import Livre, MatriceDistance,Classement
import collections
import requests
import json
from algo.betweenness import calcul_betweenness 
from algo.pagerank import calcul_pagerank


class Command(BaseCommand):
	help = 'calculer la matrice de distance Jaccard entre les livres et les classement'

	
	def handle(self,*args,**kwargs):
		seuil = 0.7
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
		self.stdout.write('The matrix Jaccard has been generated successfully')
		self.stdout.write('Sorting by betweeness ...')
		betweeness_map = calcul_betweenness(list_id,matrice_dis,0.75)
		# for k,v in betweeness_map.items():
		# 	print('k = ',k,"v=", v)
		l_between = sorted(betweeness_map.items(), key = lambda x: x[1],reverse = True)
		print("between ",l_between)
		l_between = [i[0] for i in l_between]
		self.stdout.write('The Sorting of betweeness has been caculated successfully')

		# matrice contenant le poids minimal entre deux sommets
		self.stdout.write('Calculating the matrix of the shortest path ... ')
		matrice_plus_court = calcul_plus_court(list_id,matrice_dis)
		self.stdout.write('matrix of the shortest path OK')


		self.stdout.write('Sorting by closeness ...')
		closeness_map = calcul_closeness(list_id,matrice_plus_court)
		l_close = sorted(closeness_map.items(), key = lambda x: x[1],reverse = True)
		l_close = [i[0] for i in l_close ]
		self.stdout.write('The Sorting of closeness has been caculated successfully')

		self.stdout.write('Sorting by pagerank ...')
		pagerank_map = calcul_pagerank(list_id,matrice_dis,seuil,0.15)
		l_page = sorted(pagerank_map.items(), key = lambda x: x[1],reverse = True)
		l_page = [i[0] for i in l_page ]
		self.stdout.write('The Sorting of pagerank has been caculated successfully')


		classement = Classement()
		classement.betweenness = json.dumps(l_between)
		classement.closeness = json.dumps(l_close)
		classement.pagerank = json.dumps(l_page)
		classement.save()


def calcul_closeness(list_id,matrix_plus_court):
	nb = len(list_id)
	result = {}
	for i in list_id:
		somme = 0
		for j in list_id:
			somme += matrix_plus_court[(i,j)]
		result[i] = nb/somme 
	return result



def calcul_plus_court(list_id,matrix):
	m = matrix.copy()
	for k in list_id:
		for i in list_id:
			for j in list_id:
				m[(i,j)] = min( m[(i,j)], m[(i,k)] + m[(k,j)] )
	return m 


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

