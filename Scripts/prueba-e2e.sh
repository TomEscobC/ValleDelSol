#!/bin/bash
# =============================================================================
#  Prueba End-to-End MANUAL del flujo de negocio (business core) sobre el
#  sistema en ejecucion. Evidencia para el Informe de Pruebas EP3.
#
#  Flujo: ciudadano reporta -> se persiste -> si es CRITICO -> alerta de evacuacion.
#  Requiere el backend levantado (./Scripts/levantar-local.sh).
# =============================================================================
BFF="http://localhost:8080/api/bff"
ALERTAS="http://localhost:8082/api/alertas"
ok=0; fail=0
chk() { if [ "$1" = "$2" ]; then echo "   PASA  ($3)"; ok=$((ok+1)); else echo "   FALLA ($3): esperado=$1 obtenido=$2"; fail=$((fail+1)); fi; }

echo "=================================================================="
echo " PRUEBA E2E - Valle del Sol (flujo de negocio critico)"
echo "=================================================================="

echo ""
echo "[1] Salud de los servicios"
chk "200" "$(curl -s -o /dev/null -w '%{http_code}' http://localhost:8081/api/reportes/salud)" "ms-reportes vivo"
chk "200" "$(curl -s -o /dev/null -w '%{http_code}' http://localhost:8082/api/alertas/salud)"  "ms-alertas vivo"
chk "200" "$(curl -s -o /dev/null -w '%{http_code}' $BFF/salud)"                                 "BFF vivo"

echo ""
echo "[2] Ciudadano envia un reporte CRITICO (debe generar alerta de EVACUACION)"
RESP=$(curl -s -X POST $BFF/reportes -H "Content-Type: application/json" -d '{
  "nombreCiudadano":"Vecino E2E","tipo":"INCENDIO_FORESTAL",
  "descripcion":"Foco de gran intensidad cerca de viviendas (prueba e2e).",
  "latitud":-34.715,"longitud":-71.02,"sectorReferencia":"El Bosque","gravedad":"CRITICA"}')
echo "$RESP"
ALERTA_GEN=$(echo "$RESP" | python3 -c 'import sys,json;print(json.load(sys.stdin)["alertaGenerada"])' 2>/dev/null)
NIVEL=$(echo "$RESP" | python3 -c 'import sys,json;print(json.load(sys.stdin)["alerta"]["nivel"])' 2>/dev/null)
chk "True" "$ALERTA_GEN" "se genero alerta"
chk "EVACUACION" "$NIVEL" "nivel = EVACUACION"

echo ""
echo "[3] Ciudadano envia un reporte MEDIA (NO debe generar alerta)"
RESP2=$(curl -s -X POST $BFF/reportes -H "Content-Type: application/json" -d '{
  "nombreCiudadano":"Vecino E2E","tipo":"QUEMA_NO_AUTORIZADA",
  "descripcion":"Quema de ramas controlada en el sector (prueba e2e).",
  "latitud":-34.69,"longitud":-71.04,"sectorReferencia":"Camino El Roble","gravedad":"MEDIA"}')
GEN2=$(echo "$RESP2" | python3 -c 'import sys,json;print(json.load(sys.stdin)["alertaGenerada"])' 2>/dev/null)
chk "False" "$GEN2" "MEDIA no genera alerta"

echo ""
echo "[4] Validacion (no-camino-feliz): coordenada fuera de rango debe ser rechazada"
COD=$(curl -s -o /dev/null -w '%{http_code}' -X POST $BFF/reportes -H "Content-Type: application/json" -d '{
  "nombreCiudadano":"Vecino E2E","tipo":"INCENDIO_FORESTAL",
  "descripcion":"Coordenada invalida para probar validacion del sistema.",
  "latitud":200,"longitud":-71,"sectorReferencia":"X","gravedad":"CRITICA"}')
chk "400" "$COD" "coordenada invalida = HTTP 400"

echo ""
echo "[5] Alertas activas en el motor de alertas"
curl -s $ALERTAS/activas | python3 -m json.tool 2>/dev/null | head -20

echo ""
echo "=================================================================="
echo " RESULTADO: $ok pruebas OK, $fail fallidas"
echo "=================================================================="
[ "$fail" = "0" ]
