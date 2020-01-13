from django.shortcuts import render
from django.views.generic import TemplateView, ListView
from django.db.models import Q 
from .models import Livre
def home_view(request):
	count = Livre.objects.count()
	context = {'count':count}
	
	return render(request,'home.html',context)

def livre_view(request,methode):
	if methode == 'pertinance':
		
		query = request.GET.get('motcles')
		results =  Livre.objects.filter(contenu__icontains=query) 
		context={
			'result':results,
			'query':query
		}

		return render(request,'search_result.html',context)
	if methode == 'closeness':
		query = request.GET.get('motcles')
		results =  Livre.objects.filter(contenu__icontains=query) 
		context={
			'result':results,
			'query':query,
			'methode':methode

		}

		return render(request,'search_result.html',context)

	if methode == 'betweenness':
		query = request.GET.get('motcles')
		results =  Livre.objects.filter(contenu__icontains=query) 
		context={
			'result':results,
			'query':query,
			'methode':methode
		}

		return render(request,'search_result.html',context)

	if methode == 'pagerank':
		query = request.GET.get('motcles')
		results =  Livre.objects.filter(contenu__icontains=query) 
		context={
			'result':results,
			'query':query,
			'methode':methode

		}

		return render(request,'search_result.html',context)


def detail_view(request,livre_id):

	livre = Livre.objects.get(id = livre_id)
	context = {'livre': livre}
	return render(request,'detail.html',context)

		



