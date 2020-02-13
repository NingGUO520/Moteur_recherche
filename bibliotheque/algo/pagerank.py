import sys

def calcul_pagerank(list_id,matrix,seuil,alpha):
	# il existe un lien entre 2 sommets seulement si la distance < seuil
	n = len(list_id)
	liens = set()
	for i in list_id:
		for j in list_id:
			if i == j : 
				continue 
			if (i,j) in liens or (j,i) in liens:
				continue
			if matrix[(i,j)] < seuil:
				liens.add((i,j))

# le nombre de degree pour chaque sommet 
	tabDegree = {}
	for i in list_id:
		tabDegree[i] = 0 
	for lien in liens:
		(i,j) = lien 
		tabDegree[i] += 1 
		tabDegree[j] += 1 

	# Initialiser P0 
	n = len(list_id)
	p = {}
	for id in list_id:
		p[id] = 1/n

	p = generateP(list_id,p,liens,tabDegree,alpha)	
	# print(p)
	return p 
def generateP(list_id,p0,liens,tabDegree,alpha):
	p = {}
	# initialiser p[i] = 0 pour tout id
	for i in list_id:
		p[i] = 0 
	# lire tous les liens 
	for lien in liens :
		a,b = lien 
		p[b] = p[b] + p0[a]/tabDegree[a]
		p[a] = p[a] + p0[b]/tabDegree[b]
	for id in list_id:
		p[id] = (1-alpha)*p[id] + alpha/len(list_id)
	# normalizer P 
	somme = 0 
	for k,v in p.items():
		somme +=v

	for id in list_id:
		p[id] = p[id]+(1-somme)/len(list_id)
	return p  




