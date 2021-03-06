from django.shortcuts import render
from django.views.generic import TemplateView, ListView
from django.db.models import Q 
from .models import Livre
import requests
import json

def home_view(request):
	count = Livre.objects.count()
	context = {'count':count}
	
	return render(request,'home.html',context)

def result_view(request,methode):

	query = request.GET.get('motcles')
	httpResults =  requests.get('http://localhost:8080/BooksAPI/search/'+query)
	jsonDec = json.decoder.JSONDecoder()
	if httpResults:
		httpResults = jsonDec.decode(httpResults.text)
		results = [Livre.objects.get(id = res['id']) for res in httpResults]
	else : results = []

	if methode == 'pertinance':
		context={
			'result':results,
			'query':query
		}

		return render(request,'search_result.html',context)
	else:
		results_id =  Livre.objects.sort_by(results,methode)
		results = [ Livre.objects.get(id = i) for i in results_id]
		context={
			'result':results,
			'query':query,
			'methode':methode
		}
		return render(request,'search_result.html',context)

def detail_view(request,livre_id):

	livre = Livre.objects.get(id = livre_id)
	suggestions = Livre.objects.get_suggestions(livre_id)
	suggestions_livres = [ Livre.objects.get(id = i) for i in suggestions]
	context = {
	'livre': livre,
	'suggestions':suggestions_livres
	}
	return render(request,'detail.html',context)

		



