curl -X POST http://localhost:8080/videos \
-H "Content-Type: application/json" \
-d '{
  "title": "Inception",
  "synopsis": "A mind-bending thriller about dreams within dreams.",
  "director": "Christopher Nolan",
  "castMembers": "Leonardo DiCaprio, Joseph Gordon-Levitt, Ellen Page",
  "yearOfRelease": 2010,
  "genres": ["SCI_FI", "THRILLER"],
  "runningTime": 148,
  "content": "A group of dream thieves attempt to plant an idea into the mind of a corporate heir."
}' | jq

curl -X POST http://localhost:8080/videos \
-H "Content-Type: application/json" \
-d '{
  "title": "Inception",
  "synopsis": "A mind-bending thriller about dreams within dreams.",
  "director": "Tarantino",
  "castMembers": "Leonardo DiCaprio, Joseph Gordon-Levitt, Ellen Page",
  "yearOfRelease": 2010,
  "genres": ["SCI_FI", "THRILLER"],
  "runningTime": 148,
  "content": "A group of dream thieves attempt to plant an idea into the mind of a corporate heir."
}' | jq

curl -X POST http://localhost:8080/videos \
-H "Content-Type: application/json" \
-d '{
  "title": "The Matrix",
  "synopsis": "A hacker discovers the reality he lives in is a simulated illusion controlled by machines.",
  "director": "Lana Wachowski, Lilly Wachowski",
  "castMembers": "Keanu Reeves, Laurence Fishburne, Carrie-Anne Moss",
  "yearOfRelease": 1999,
  "genres": ["SCI_FI", "ACTION"],
  "runningTime": 136,
  "content": "Neo learns the truth about the Matrix and joins a rebellion against its controllers."
}' | jq