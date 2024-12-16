curl -X GET "http://localhost:8080/videos/filtered?title=Inception&yearOfRelease=2010&director=Christopher%20Nolan" \
-H "Content-Type: application/json" | jq
