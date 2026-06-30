#!/bin/bash
# =============================================================================
#  Valle del Sol - Levantar la solucion completa en local (EP3)
#  Levanta: PostgreSQL (Docker) + ms-reportes (8081) + ms-alertas (8082) + BFF (8080)
#  Uso:  ./Scripts/levantar-local.sh
# =============================================================================
set -e
cd "$(dirname "$0")/.."   # raiz del proyecto

# --- 1. Forzar Java 21 (Spring Boot 3.1.5 no es compatible con Java 25) ---
if /usr/libexec/java_home -v 21 >/dev/null 2>&1; then
  export JAVA_HOME="$(/usr/libexec/java_home -v 21)"
  echo "Usando JAVA_HOME=$JAVA_HOME"
else
  echo "ADVERTENCIA: no se encontro JDK 21. Instala Temurin 21 o ajusta JAVA_HOME."
fi

LOG_DIR="./.run-logs"; mkdir -p "$LOG_DIR"

# --- 2. Base de datos PostgreSQL en Docker (puerto 5434 del host) ---
echo "==> Levantando PostgreSQL (docker compose)..."
docker compose up -d
echo -n "    Esperando que la BD acepte conexiones"
for i in $(seq 1 30); do
  if docker exec vallesol_db pg_isready -U admin_vallesol -d db_incendios_forestales >/dev/null 2>&1; then echo " OK"; break; fi
  echo -n "."; sleep 1
done

# --- 3. Compilar (sin pruebas) si faltan los jar ---
build_if_needed() {
  local dir="$1"; local jar
  jar=$(ls "$dir"/target/*-1.0.0-SNAPSHOT.jar 2>/dev/null | grep -v original | head -1 || true)
  if [ -z "$jar" ]; then
    echo "==> Compilando $dir ..."
    (cd "$dir" && mvn -q -B -DskipTests package)
  fi
}
build_if_needed ms-reportes-ciudadano
build_if_needed ms-alertas-comunidad
build_if_needed bff-valle-sol

run() { # run <dir> <puerto> <ruta-salud>
  local dir="$1" port="$2" salud="$3"
  local jar; jar=$(ls "$dir"/target/*-1.0.0-SNAPSHOT.jar | grep -v original | head -1)
  echo "==> Iniciando $dir en :$port"
  nohup java -jar "$jar" > "$LOG_DIR/$(basename "$dir").log" 2>&1 &
  echo $! > "$LOG_DIR/$(basename "$dir").pid"
  for i in $(seq 1 40); do
    [ "$(curl -s -o /dev/null -w '%{http_code}' "http://localhost:$port$salud")" = "200" ] && { echo "    $dir OK"; return; }
    sleep 1
  done
  echo "    ADVERTENCIA: $dir no respondio a tiempo (ver $LOG_DIR/$(basename "$dir").log)"
}

# --- 4. Microservicios y BFF ---
run ms-reportes-ciudadano 8081 /api/reportes/salud
run ms-alertas-comunidad  8082 /api/alertas/salud
run bff-valle-sol         8080 /api/bff/salud

echo ""
echo "============================================================"
echo " Backend arriba:"
echo "   - ms-reportes  : http://localhost:8081/api/reportes"
echo "   - ms-alertas   : http://localhost:8082/api/alertas"
echo "   - BFF          : http://localhost:8080/api/bff/dashboard"
echo " Frontend (en otra terminal):"
echo "   cd frontend-valle-sol && npm install && npm start"
echo "   -> http://localhost:4200"
echo " Para detener todo:  ./Scripts/detener-local.sh"
echo "============================================================"
