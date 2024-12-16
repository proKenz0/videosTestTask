curl -X GET "http://localhost:8080/videos/filtered?genres=THRILLER&genres=SCI_FI" \
-H "Content-Type: application/json" | jq
