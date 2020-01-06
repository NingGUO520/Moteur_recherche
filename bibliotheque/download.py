import requests
total = 100
i = 0 
cpt = 0 
while cpt < 10:
	
	num = 10000+i
	filename = str(num)+'.txt'
	url = 'http://www.gutenberg.org/files/'+str(num)+'/'+filename
	r = requests.get(url, allow_redirects=True)
	print('code = ', r.status_code)
	if r.status_code != 200 :
		i += 1 
		continue 
	data = r.text.split('\n')
	for line in data:
		if line.startswith('Title:'):
			print(line[7:])
		if line.startswith('Author:'):
			print(line[8:])

		if line.startswith('Language:'):
			print(line[10:])

	print('>>>>>>>>>>>>>>>the book ',filename,' has been download successfully')
	i += 1 
	cpt += 1 