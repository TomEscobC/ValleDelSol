#!/bin/bash
# Detiene el backend de Valle del Sol y la base de datos.
cd "$(dirname "$0")/.."
echo "==> Deteniendo servicios Java..."
for n in ms-reportes-ciudadano ms-alertas-comunidad bff-valle-sol; do
  if [ -f ".run-logs/$n.pid" ]; then kill "$(cat ".run-logs/$n.pid")" 2>/dev/null && echo "    $n detenido"; rm -f ".run-logs/$n.pid"; fi
done
# Respaldo: matar por nombre de jar por si quedaron procesos
pkill -f 'ms-reportes-ciudadano-1.0.0' 2>/dev/null || true
pkill -f 'ms-alertas-comunidad-1.0.0' 2>/dev/null || true
pkill -f 'bff-valle-sol-1.0.0' 2>/dev/null || true
echo "==> Deteniendo PostgreSQL (docker compose)..."
docker compose down
echo "Listo."
