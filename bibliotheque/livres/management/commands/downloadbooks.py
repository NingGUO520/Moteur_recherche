from django.core.management.base import BaseCommand
from django.utils import timezone
from livres.models import Livre


class Command(BaseCommand):
	help = 'insert books into database'

	def add_arguments(self,parser):
		parser.add_argument('total',type=int,help='Indicates the number of books to be inserted')

	def handle(self,*args,**kwargs):
		total = kwargs['total']
		for i in range(total):
			livre = Livre()
			livre.titre = 'book'+str(i)
			livre.contenu = 'book'+str(i)
			livre.save()

		self.stdout.write('%d books have been inserted successfully'%total)
