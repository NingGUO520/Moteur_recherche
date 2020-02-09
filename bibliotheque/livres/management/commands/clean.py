from django.core.management.base import BaseCommand

from livres.models import Livre, MatriceDistance,Classement

class Command(BaseCommand):
	help = 'clean the database'

	
	def handle(self,*args,**kwargs):
		MatriceDistance.objects.all().delete()
		Classement.objects.all().delete()
		self.stdout.write('the database of MatriceDistance and Classement have been cleaned')