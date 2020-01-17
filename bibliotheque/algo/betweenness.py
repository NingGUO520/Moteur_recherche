import sys

def calcul_betweenness(list_id,matrix,seuil):	
	paths= {}
	# completer matrice d'adjacent
	m = {}
	for i in list_id:
		for j in list_id:
			if i == j :
				m[(i,j)] = 0 
			elif matrix[(i,j)] >= seuil :
				m[(i,j)] = sys.maxsize
			else :
				if (j,i) in matrix:
					m[(i,j)] = matrix[(j,i)]
				elif (i,j) in matrix :
					m[(i,j)] = matrix[(i,j)]
				else :
					m[(i,j)] = sys.maxsize

	# initialisation Path
	for i in list_id:
		for j in list_id:
			if i == j :
				continue
			paths[(i,j)] = [j]

	# calculer les plus courts chemins
	for k in list_id:
		for i in list_id:
			for j in list_id:
				if i==j or i == k or j == k :
					continue

				if m[(i,k)] + m[(k,j)] < m[(i,j)] :
					m[(i,j)] = m[(i,k)] + m[(k,j)]
					paths[(i,j)] = paths[(i,k)] 
				elif not m[(i,j)] == sys.maxsize and m[(i,k)] + m[(k,j)] == m[(i,j)] :
					l = paths[(i,j)].copy()
					for ele in paths[(i,k)]:
						if not ele in l :
							l.append(ele)
					paths[(i,j)] = l 
	chemins = DFS(list_id, paths)
	# print("chemins")
	# for k,v in chemins.items():
	# 	print('k=',k,'v=',v)
	g = {}
	for v in list_id:
		somme = 0 
		for s in list_id:
			if s == v :
				continue 
			for t in list_id:
				if t == v or t == s:
					continue
				if (s,t) in chemins:
					nb_s_t = len(chemins[(s,t)])
					if nb_s_t > 0 :
						cpt = 0 
						for path in chemins[(s,t)]:
							if v in path :
								cpt +=1 

						cout = cpt/nb_s_t
						# print('v = ',v)
						# print('s=',s,'t=',t,'cout=',cout)
						somme += cout/2 

		g[v] = somme
	return g 


# convertir de paths[i,j]=liste à
 # chemins[i,j] = une liste de liste des sommets intermediares entre i et j 
def DFS(list_id,paths):
	chemins = {}
	for i in list_id:
		for j in list_id:
			answers = []
			getAnswers(i,j,[],paths,answers)
			if len(answers)>0 :
				chemins[(i,j)] = answers

	return chemins

	
	
def getAnswers(i,j,answer,paths, answers):
	if i==j:
		if len(answer)>0 and not answer in answers:
			answers.append(answer[:-1])


	else:
		currents = paths[(i,j)]
		
		for current in currents:
			answer2 = answer.copy()
			answer2.append(current)
			getAnswers(current,j,answer2,paths,answers)


# l = ['a','b','c','d','e','f','g','h']
# matrix = {('a','b'):1,('b','c'):1,('e','b'):1,('f','b'):1,('e','d'):1,('e','g'):1,('e','f'):1,('f','h'):1,('g','f'):1,}

# p = calcul_betweenness(l,matrix)
# for pa,v in p.items() :
# 	print('k=',pa,'v=',v)
