# fonction qui permet de calculer la distance de Jaccard entre deux documents	  
# dic1 le premier document (livre)
# dic2 le deuxième document (livre)
import collections
def get_dic(txt):
	return collections.Counter(txt.replace(',',' ').replace('.',' ').split())

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

	

# u = "bien manger midi"
# t = "bien manger matin"
# w = "leve matin"
# s = "bien dormir matin"

# dicu = get_dic(u)
# dic_t = get_dic(t)
# dic_w = get_dic(w)
# dics = get_dic(s)

# print("u",dicu)
# print("t",dic_t)
# print("w",dic_w)
# print(dis_jaccard(dicu,dic_t))
# print(dis_jaccard(dic_t,dic_w))
# print(dis_jaccard(dics,dic_t))
# print(dis_jaccard(dics,dicu))

