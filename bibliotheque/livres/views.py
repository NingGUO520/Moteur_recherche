from django.shortcuts import render
from django.views.generic import TemplateView, ListView
from django.db.models import Q 
from .models import Livre
class home_view(TemplateView):
	
	template_name = 'home.html'

def livre_view(request,methode):
	print('methode',methode)
	query = request.POST.get('motcles')
	results =  Livre.objects.filter(contenu__icontains=query) 
	context={'result':results}

	return render(request,'search_result.html',context)

def detail_view(request,livre_id):

	livre = Livre.objects.get(id = livre_id)
	context = {'livre': livre}
	return render(request,'detail.html',context)

		



