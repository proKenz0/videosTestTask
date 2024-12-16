curl -X PUT http://localhost:8080/videos/1 \
-H "Content-Type: application/json" \
-d '{
  "title": "Inception",
  "synopsis": "A mind-bending thriller about dreams within dreams.",
  "director": "Christopher Nolan",
  "castMembers": "Leonardo DiCaprio, Joseph Gordon-Levitt, Ellen Page",
  "yearOfRelease": 2010,
  "genres": ["SCI_FI", "ACTION"],
  "runningTime": 145
}' | jq
