# Tests del módulo app

Este archivo describe cómo ejecutar y depurar los tests unitarios (incluyendo Robolectric/Compose) del módulo `app` y cómo se ejecutan en CI.

Requisitos locales
- JDK 17 (el proyecto usa toolchain Java 17).
- Gradle wrapper incluido en el repo (`./gradlew`).

Comandos útiles

- Ejecutar todos los unit tests del módulo `app` (Robolectric + Compose):

```bash
# Desde la raíz del repo
./gradlew :app:testDebugUnitTest --info
```

- Compilar solo las pruebas (rápido):

```bash
./gradlew :app:compileDebugUnitTestKotlin --no-daemon --stacktrace
```

- Limpiar, compilar app y ejecutar tests (guardar salida en un log):

```bash
./gradlew clean :app:assembleDebug :app:testDebugUnitTest --no-daemon --info --stacktrace > /tmp/mundo-tests.log 2>&1
# Mostrar últimas líneas del log
tail -n 300 /tmp/mundo-tests.log
```

Recomendaciones y problemas comunes

- Java / Toolchain:
  - Asegúrate de usar JDK 17 cuando ejecutes Gradle localmente. Usa `java -version` para comprobarlo.
  - Si necesitas forzar la JDK para Gradle, añade en `gradle.properties` (no incluido por defecto):
    `org.gradle.java.home=/ruta/a/jdk17`

- Recursos Android en pruebas unitarias (Robolectric):
  - El módulo `app` tiene `testOptions.unitTests.isIncludeAndroidResources = true`, lo que permite a Robolectric resolver `R.string`, layouts y drawables.

- Tests que inician Firebase, Crashlytics o la base de datos real:
  - Evita lanzar `MainActivity` en tests unitarios; utiliza tests de composición (Compose) y fakes para servicios.
  - Para pruebas que realmente requieran el framework Android o servicios nativos, usa `androidTest` en un dispositivo/emulador.

CI (GitHub Actions)

Se incluye un ejemplo de workflow `./github/workflows/android-unit-tests.yml` que instala Java 17 y ejecuta los unit tests del módulo `app`.

Debugging

- Si los tests fallan en CI y la salida es larga, redirige la salida a un archivo y revisa las últimas 300 líneas para identificar la causa.
- Si ves errores relacionados con MockK y ClassLoader en Robolectric, prueba a actualizar MockK o añadir `mockk-agent-jvm` en dependencias de test.

Contacto

Si algo falla al ejecutar los tests, copia las últimas 200-400 líneas del log y pégalas aquí para que lo depuremos juntos.

