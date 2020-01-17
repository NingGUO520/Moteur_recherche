import requests

r = requests.get('http://localhost:8080/BooksTomCat/search/people')

print(r.json)

