from django.shortcuts import render
from django.views.generic import TemplateView, ListView
from django.db.models import Q 
from .models import Livre

class home_view(TemplateView):
	
	template_name = 'home.html'

def livre_view(request):
	query = request.GET.get('motcles')
	results =  Livre.objects.filter(contenu__icontains=query) 
	context={'result':results}

	return render(request,'search_result.html',context)
		


