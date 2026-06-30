# 🎬 Guiones de los videos — EP3 Valle del Sol

Dos videos (uno por grupo), entrega máxima **29-06-2026 23:59**:
1. **Video de Arquitectura** (~3–4 min)
2. **Video de Uso / Demo del sistema** (~4–5 min)

> Consejo: graba la pantalla con QuickTime (⇧⌘5) o Loom. Habla pausado. Ten **todo levantado antes** de grabar
> (`./Scripts/levantar-local.sh` + `cd frontend-valle-sol && npm start`). Ten el diagrama
> `Documentacion/diagramas/arquitectura-microservicios.png` abierto en una pestaña.

---

## 🎬 VIDEO 1 — ARQUITECTURA

### Escena 1 — Presentación (15 s)
**[EN PANTALLA]** Portada / diagrama de arquitectura.
**NARRADOR:**
> "Somos Matías Ampuero y Tomás Escobar. Presentamos la arquitectura de microservicios de **Valle del Sol**, la plataforma de gestión de emergencias de incendios forestales para la Municipalidad, asignatura Desarrollo Fullstack III."

### Escena 2 — El caso y el porqué de microservicios (30 s)
**[EN PANTALLA]** Diagrama de arquitectura completo.
**NARRADOR:**
> "El problema: la detección de incendios tarda en promedio 35 minutos y las alertas a la comunidad llegan tarde. Nuestra solución reduce ese tiempo permitiendo que cualquier vecino **reporte un foco con geolocalización**, y que el sistema **alerte automáticamente a la comunidad** cuando el reporte es crítico.
> Elegimos **microservicios** para que el reporte ciudadano y el motor de alertas escalen y evolucionen de forma independiente."

### Escena 3 — Componentes (60 s)
**[EN PANTALLA]** Señala cada caja del diagrama mientras hablas.
**NARRADOR:**
> "La arquitectura tiene cuatro componentes desplegables:
> - El **Frontend en Angular**, empaquetado como componente NPM. Es la cara del ciudadano y del Centro de Comando, y consume **únicamente** el BFF.
> - El **BFF — Backend For Frontend** — en Spring Boot. Es la única puerta de entrada del frontend: **orquesta** el flujo de negocio y **agrega** la información para el dashboard.
> - El microservicio **ms-reportes-ciudadano**, con Spring Boot y JPA, que administra los reportes con sus reglas de negocio.
> - El microservicio **ms-alertas-comunidad**, que genera las alertas informativas, preventivas y de evacuación.
> - Y **PostgreSQL** en Docker, donde se **persisten** los reportes y las alertas."

### Escena 4 — Comunicación y patrones (45 s)
**[EN PANTALLA]** Las flechas del diagrama.
**NARRADOR:**
> "La comunicación es **REST sobre JSON**. El frontend habla solo con el BFF; el BFF llama por REST a los dos microservicios; y cada microservicio persiste con **JPA/Hibernate** en PostgreSQL.
> Aplicamos los patrones **Backend For Frontend**, **microservicios independientes** con base de datos centralizada por tablas, y **API REST**. El BFF además habilita **CORS** para el frontend y maneja la caída de un microservicio con una respuesta controlada."

### Escena 5 — Flujo crítico y versionamiento (40 s)
**[EN PANTALLA]** Diagrama de flujo `flujo-business-core.png` y luego GitHub.
**NARRADOR:**
> "El proceso de negocio más importante es: un ciudadano reporta un foco, **se persiste**, y si la gravedad es **alta o crítica**, el BFF genera **automáticamente** una alerta de evacuación para la comunidad.
> Todo el código está versionado en **GitHub**, con una estrategia de branching (main, develop y ramas feature)."

**NARRADOR (cierre):**
> "Esta arquitectura cumple los requerimientos de seguridad por capas, escalabilidad y mantenibilidad del caso. Gracias."

---

## 🎬 VIDEO 2 — USO / DEMO DEL SISTEMA

> **Antes de grabar:** ten levantado el backend (`./Scripts/levantar-local.sh`), el frontend (`npm start`)
> y abierta una terminal extra. Ten Docker corriendo.

### Escena 1 — Introducción (15 s)
**[EN PANTALLA]** El frontend en `http://localhost:4200`.
**NARRADOR:**
> "En este video mostramos el **uso del sistema Valle del Sol** funcionando de extremo a extremo: el reporte ciudadano, la generación automática de alertas, la persistencia en base de datos y las pruebas."

### Escena 2 — El ciudadano reporta una emergencia crítica (75 s)
**[EN PANTALLA]** Click en **Modo: Vecino** → **Reportar Emergencia**.
**NARRADOR:**
> "Entramos como vecino y vamos a *Reportar Emergencia*. Completamos el formulario: nombre, tipo *Incendio forestal*, gravedad **Crítica**, el sector, y la descripción. Usamos el botón de **geolocalización** para tomar la ubicación."
**[ACCIÓN]** Llena el formulario con gravedad **Crítica** y presiona **Enviar reporte**.
**NARRADOR:**
> "Al enviarlo, el sistema responde con el **folio del reporte** y, como era **crítico**, generó **automáticamente una alerta de EVACUACIÓN** para la comunidad. Esto es el flujo de negocio central funcionando."

### Escena 3 — El Centro de Comando ve el dato en vivo (45 s)
**[EN PANTALLA]** Cambia a **Modo: Admin** → **Inicio / Dashboard**.
**NARRADOR:**
> "Ahora, como operador del Centro de Comando, vemos el **dashboard**: los totales de reportes, los focos críticos y las **alertas activas** se actualizan en vivo, leyendo desde el BFF. Arriba aparece la **alerta de evacuación** que acabamos de generar, y en la tabla, el reporte recién creado, traído desde PostgreSQL."

### Escena 4 — Persistencia real en la base de datos (40 s)
**[EN PANTALLA]** Terminal: ejecuta los dos `docker exec ... psql`.
```bash
docker exec vallesol_db psql -U admin_vallesol -d db_incendios_forestales -c \
  "SELECT id, nombre_ciudadano, gravedad, estado, sector_referencia FROM reportes ORDER BY id;"
docker exec vallesol_db psql -U admin_vallesol -d db_incendios_forestales -c \
  "SELECT id, nivel, zona, reporte_origen_id, activa FROM alertas ORDER BY id;"
```
**NARRADOR:**
> "Para demostrar la **persistencia**, consultamos directamente PostgreSQL. Aquí está el reporte que creamos, y aquí la alerta de evacuación, enlazada al reporte por su `reporte_origen_id`. El dato **quedó guardado**."

### Escena 5 — Evidencia de pruebas (75 s)
**[EN PANTALLA]** Terminal: corre la prueba end-to-end y luego `mvn verify`.
```bash
./Scripts/prueba-e2e.sh
```
**NARRADOR:**
> "Ejecutamos la **prueba end-to-end** sobre el sistema en ejecución: verifica que un reporte crítico genere alerta, que uno de gravedad media **no** la genere, y que una coordenada inválida sea **rechazada** con HTTP 400. Resultado: **7 verificaciones OK**."

**[EN PANTALLA]** Luego:
```bash
cd ms-reportes-ciudadano && mvn clean verify
```
**NARRADOR:**
> "Y estas son las **pruebas automatizadas** del microservicio: unitarias e integración, todas en verde. En total el proyecto tiene **27 pruebas** —unitarias, de integración y end-to-end— con su reporte de cobertura JaCoCo. Las pruebas no son solo del camino feliz: incluyen validaciones, errores y un bug que detectamos y corregimos (coordenadas fuera de rango)."

### Escena 6 — Cierre (20 s)
**NARRADOR:**
> "En resumen: el sistema integra frontend y backend por REST, persiste en PostgreSQL, automatiza la alerta de evacuación ante reportes críticos y está respaldado por pruebas en sus tres niveles. Gracias."

---

## ✅ Checklist antes de subir
- [ ] Video 1 (Arquitectura) grabado y exportado.
- [ ] Video 2 (Uso/Demo) grabado y exportado.
- [ ] Código subido a GitHub (rama `main` o `develop`).
- [ ] Informe de pruebas (`Informe_Pruebas_EP3.pdf`) adjunto.
- [ ] Diagramas incluidos.
