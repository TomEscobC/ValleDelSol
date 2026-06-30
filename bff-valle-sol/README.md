# Backend For Frontend (BFF) - Valle del Sol
Componente backend generado a partir de Arquetipos Maven para asegurar la coherencia arquitectónica.

## Instrucciones de Ejecución
1. Asegúrate de tener la base de datos PostgreSQL corriendo en Docker.
2. Abre la terminal en esta carpeta y compila el proyecto con: `mvn clean install`
3. Para correr las pruebas unitarias ejecuta: `mvn test`

## Guía de Arquetipos
Para generar un nuevo microservicio utilizando la misma estructura base estandarizada, utiliza el siguiente comando:
`mvn archetype:generate -DgroupId=cl.duoc.vallesol.ms -DartifactId=NOMBRE_NUEVO_MS -DarchetypeArtifactId=maven-archetype-quickstart`