# Capacity Planning Tool

Strategic capacity planning prototype — Java 21 / Spring Boot microservices + Svelte frontend.

## Quick start (Docker)

```bash
docker-compose up --build
```

Open **http://localhost** in your browser. Sample data is loaded automatically.

## Services

| Service | Docker URL | Local URL | Description |
|---|---|---|---|
| Frontend | http://localhost | http://localhost:5173 | Svelte SPA |
| team-service | http://localhost:18083 | http://localhost:8083 | Teams, persons, capacity |
| planning-service | http://localhost:18082 | http://localhost:8082 | Initiatives, epics, planning |

## Local dev (no Docker)

Requires: Java 21, Maven, Node 20, PostgreSQL with `team_db` and `planning_db` databases.

You can reuse the Docker PostgreSQL container instead of a local install:

```bash
docker-compose up postgres   # starts only the DB
```

Then in separate terminals:

```bash
# Start team-service
cd team-service && mvn spring-boot:run

# Start planning-service
cd planning-service && mvn spring-boot:run

# Start frontend (connects to local services on ports 8083 / 8082)
cd frontend && npm install && npm run dev
# → http://localhost:5173
```

To run the frontend against the Dockerised backend instead:

```bash
cd frontend && npm run dev:docker
# → http://localhost:5173  (proxies to ports 18083 / 18082)
```

## Build individual images

```bash
docker build -t capacity-team-service     ./team-service
docker build -t capacity-planning-service ./planning-service
docker build -t capacity-frontend         ./frontend
```

See [design-document.md](./design-document.md) for architecture decisions and extensibility notes.
