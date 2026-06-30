#!/bin/bash

# Variables de conexión local al Docker
DB_USER="admin_vallesol"
export PGPASSWORD="SecurePass2026!"
DB_NAME="db_incendios_forestales"
DB_HOST="localhost"
DATE=$(date +"%Y-%m-%d_%H-%M")
FILE_NAME="backup_${DB_NAME}_${DATE}.sql.gz"

echo "Iniciando respaldo de base de datos..."

# El comando se conecta al contenedor local y genera el backup
docker exec -e PGPASSWORD="SecurePass2026!" vallesol_db pg_dump -U admin_vallesol -d db_incendios_forestales | gzip > ./scripts/$FILE_NAME

echo "¡Respaldo completado! Archivo generado: $FILE_NAME"