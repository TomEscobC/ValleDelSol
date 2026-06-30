# 🌲 Valle del Sol — Plataforma de Gestión de Emergencias

Solución de **microservicios** para la **Municipalidad Valle del Sol – Subdirección de Gestión de Emergencias**.
Permite a la comunidad **reportar focos de incendio** con geolocalización y, ante reportes críticos,
**genera automáticamente alertas de evacuación** para la comunidad. Incluye un **Dashboard / Centro de Comando**
para el monitoreo en tiempo real.

> **Asignatura:** Desarrollo Fullstack III (DSY1106) · **EP3 — Integración de arquitectura de microservicios**
> **Integrantes:** Matías Ampuero · Tomás Escobar · **Docente:** Israel Villagra Riquelme

---

## 🏗️ Arquitectura

![Arquitectura](Documentacion/diagramas/arquitectura-microservicios.png)

| Componente | Tecnología | Puerto | Rol |
|---|---|---|---|
| `frontend-valle-sol` | Angular 21 (NPM) | 4200 | UI ciudadano + Centro de Comando. Consume **solo** el BFF. |
| `bff-valle-sol` | Spring Boot | 8080 | **Backend For Frontend**: orquesta el flujo de negocio y agrega datos. |
| `ms-reportes-ciudadano` | Spring Boot + JPA | 8081 | Microservicio de reportes ciudadanos. |
| `ms-alertas-comunidad` | Spring Boot + JPA | 8082 | Microservicio Motor de Alertas a la Comunidad. |
| PostgreSQL 15 | Docker | 5434 | Persistencia (`db_incendios_forestales`). |

**Flujo de negocio crítico:** un ciudadano reporta un foco → se persiste → si la gravedad es **ALTA/CRÍTICA**,
el BFF genera automáticamente una **alerta de evacuación** en el motor de alertas.

---

## ✅ Requisitos

- **Java 21** (Temurin/Adoptium). ⚠️ *No usar Java 25: Spring Boot 3.1.5 no es compatible.*
- **Maven 3.9+**
- **Docker** + Docker Compose
- **Node 20+** y npm (para el frontend)

---

## 🚀 Puesta en marcha

### Opción A — Scripts (recomendado)

```bash
# Backend completo (PostgreSQL + 3 servicios)
./Scripts/levantar-local.sh

# Frontend (en otra terminal)
cd frontend-valle-sol && npm install && npm start   # http://localhost:4200

# Detener todo
./Scripts/detener-local.sh
```

### Opción B — Manual

```bash
# 1. Base de datos
docker compose up -d

# 2. Microservicios (cada uno en su terminal, con Java 21)
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
cd ms-reportes-ciudadano && mvn spring-boot:run
cd ms-alertas-comunidad  && mvn spring-boot:run
cd bff-valle-sol         && mvn spring-boot:run

# 3. Frontend
cd frontend-valle-sol && npm install && npm start
```

---

## 🧪 Pruebas

```bash
# Unitarias + integración + cobertura (por microservicio)
cd ms-reportes-ciudadano && mvn clean verify
cd ms-alertas-comunidad  && mvn clean verify
cd bff-valle-sol         && mvn clean verify     # incluye prueba end-to-end

# Prueba end-to-end manual sobre el sistema en ejecución
./Scripts/prueba-e2e.sh
```

**27 pruebas** (17 unitarias · 8 de integración · 2 end-to-end), todas en verde.
Reporte de cobertura JaCoCo: `*/target/site/jacoco/index.html`.
Detalle completo en **[Documentacion/Informe_Pruebas_EP3.pdf](Documentacion/Informe_Pruebas_EP3.pdf)**.

---

## 📡 Endpoints principales

| Método | Endpoint | Descripción |
|---|---|---|
| `POST` | `:8080/api/bff/reportes` | Registra un reporte (orquesta alerta si es crítico) |
| `GET`  | `:8080/api/bff/dashboard` | Datos agregados del Centro de Comando |
| `POST` | `:8081/api/reportes` | Crea y persiste un reporte |
| `PATCH`| `:8081/api/reportes/{id}/estado` | Cambia el estado de un reporte |
| `POST` | `:8082/api/alertas/generar` | Genera una alerta desde un reporte |
| `GET`  | `:8082/api/alertas/activas` | Lista las alertas activas |

Ejemplo (reporte crítico → alerta de evacuación):

```bash
curl -X POST http://localhost:8080/api/bff/reportes -H "Content-Type: application/json" -d '{
  "nombreCiudadano":"Tomás Escobar","tipo":"INCENDIO_FORESTAL",
  "descripcion":"Foco activo avanzando hacia viviendas en El Bosque.",
  "latitud":-34.715,"longitud":-71.02,"sectorReferencia":"El Bosque","gravedad":"CRITICA"}'
```

---

## 📁 Estructura

```
ValleDelSol_EP3/
├── bff-valle-sol/            # BFF (Spring Boot)
├── ms-reportes-ciudadano/    # Microservicio de reportes (Spring Boot + JPA)
├── ms-alertas-comunidad/     # Microservicio de alertas (Spring Boot + JPA)
├── frontend-valle-sol/       # Frontend (Angular 21)
├── Scripts/                  # levantar / detener / prueba-e2e / backup
├── Documentacion/            # Informe de pruebas, diagramas, documentos EP1/EP2
└── docker-compose.yml        # PostgreSQL
```
