# GamesUP

## Environment
JDK 21
Python 3.12

## Dev

### Spring server
```
cd server
./mvnw spring-boot:run
```

### Python server
```
cd CodeApiPython
pip install -r requirements.txt
fastapi dev main.py
```

## Start
```
cd games-up
docker compose up
```

## Test and coverage
```
cd server
./mvnw clean test verify
```

The coverage report is available at `./target/site/jacoco/index.html`