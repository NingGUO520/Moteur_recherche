import requests

num = 10000
filename = str(num)+'.txt'
url = 'http://www.gutenberg.org/files/'+str(num)+'/'+filename
r = requests.get(url, allow_redirects=True)
data = r.text.split('\n')
for line in data:
	if line.startswith('Title:'):
		print(line[7:])
	if line.startswith('Author:'):
		print(line[8:])

	if line.startswith('Language:'):
		print(line[10:])
	
print('>>>>>>>>>>>>>>>the book ',filename,' has been download successfully')