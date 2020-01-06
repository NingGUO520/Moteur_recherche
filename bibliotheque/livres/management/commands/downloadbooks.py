from django.core.management.base import BaseCommand
from django.utils import timezone
from livres.models import Livre
import requests


class Command(BaseCommand):
	help = 'insert books into database'

	def add_arguments(self,parser):
		parser.add_argument('total',type=int,help='Indicates the number of books to be inserted')

	def handle(self,*args,**kwargs):
		num = 10000
		total = kwargs['total']
		i = 0 
		cpt = 0 
		while cpt < total:
			
			num = 10000+i
		
			livre = Livre()

			filename = str(num)+'.txt'
			url = 'http://www.gutenberg.org/files/'+str(num)+'/'+filename
			r = requests.get(url, allow_redirects=True)
			if r.status_code != 200 :
				i += 1 
				continue 
			data = r.text.split('\n')
			for line in data:
				if line.startswith('Title:'):
					livre.titre = line[7:]
				if line.startswith('Author:'):
					livre.auteur = line[8:]

				if line.startswith('Language:'):
					livre.language = line[10:]
		
			livre.contenu = r.text
			livre.save()
			i += 1 
			cpt += 1 
			self.stdout.write('book %s has been inserted successfully'%filename)

		self.stdout.write('>>>>>>>>>>>>>>>>> %d books have been inserted successfully'%total)

def telecharger(num):

	
	print('>>>>>>>>>>>>>>>the book ',filename,' has been download successfully')