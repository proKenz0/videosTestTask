# How to run project

## Profile

By default, is used profile 'linux'. Use it if you use Linux OS.
'linux' profile will automatically create and run postgres in docker container and create db for project.
Use 'default' profile if you run app in other OS.
With 'default' profile starting a PostgreSQL server and create db is your own responsibility.

## Run App

Use `mvn spring-boot:run` in the root of your project to run the app.

## Advise

You can use curls or scripts for testing code from 'requests' located in root of project.