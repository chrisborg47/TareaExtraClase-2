# Práctica: Consumo de API REST con Java Swing

Proyecto universitario desarrollado en **Java** con interfaz gráfica **Swing/JFrame**, consumo de API REST con **HttpURLConnection** y procesamiento de JSON con **Gson**.

## 1) Estructura del proyecto

```text
api-practice/
├── build.xml
├── lib/
│   └── (se descarga automáticamente gson-2.10.jar al compilar con Ant)
├── nbproject/
│   └── project.properties
├── src/
│   └── api/practice/
│       ├── ApiPractice.java
│       ├── model/
│       │   └── User.java
│       ├── service/
│       │   ├── ApiServiceException.java
│       │   └── DummyJsonApiService.java
│       └── ui/
│           └── MainFrame.java
└── README.md
```

## 2) Explicación breve para el entregable

### ¿Qué es una API?
Una **API (Application Programming Interface)** es un mecanismo que permite que dos sistemas se comuniquen entre sí. En esta práctica, la API pública **DummyJSON** entrega información de usuarios a nuestra aplicación Java.

### ¿Qué es JSON?
**JSON (JavaScript Object Notation)** es un formato de texto ligero para intercambiar datos estructurados. Una API suele responder en JSON y luego el programa lo interpreta para convertirlo en objetos (por ejemplo, `User`).

### ¿Qué es una petición HTTP?
Una petición HTTP es una solicitud enviada por un cliente a un servidor. En esta práctica se usa el método **GET** para consultar datos:
- `GET https://dummyjson.com/users`
- `GET https://dummyjson.com/users/{id}`

## 3) Funcionalidades implementadas

La interfaz gráfica incluye las 4 opciones solicitadas:

1. **Mostrar todos los usuarios**
   - Consulta `/users`
   - Muestra en JTable: ID, nombre completo, edad, email.

2. **Buscar usuario por ID**
   - Valida campo no vacío
   - Valida formato numérico
   - Consulta `/users/{id}`
   - Muestra: nombre, apellido, edad, email, teléfono, ciudad.

3. **Mostrar correos de los usuarios**
   - Consulta `/users`
   - Extrae únicamente los correos y los muestra en área de texto.

4. **Salir**
   - Cierra la aplicación correctamente.

## 4) Manejo de errores implementado

Se cubren estos casos:
- Error de conexión o internet
- Error de respuesta HTTP
- Usuario no encontrado
- ID vacío o no numérico
- Respuesta vacía
- Error al parsear JSON

Los mensajes se muestran en `JOptionPane` y en el área de resultados para mejor retroalimentación.

## 5) Instrucciones de ejecución

### Opción A: NetBeans (recomendada para JFrame Form)
1. Abrir NetBeans.
2. Seleccionar **Open Project** y elegir la carpeta `api-practice`.
3. Ejecutar el proyecto (`Run`).
4. Si NetBeans solicita resolver dependencias, ejecuta primero `ant clean compile` para descargar Gson automáticamente.

### Opción B: Línea de comandos (Ant)
Desde la carpeta `api-practice` ejecutar:

```bash
ant clean compile   # descarga Gson automáticamente si no existe
ant run
```

## 6) Recomendaciones para tomar capturas de ejecución

Para evidencias de la práctica, tomar capturas de:

1. **Pantalla principal** mostrando botones, campo ID, tabla y área de resultados.
2. **Opción 1** con tabla llena de usuarios.
3. **Opción 2** buscando un ID válido (por ejemplo 5) mostrando detalle completo.
4. **Opción 2 con validación** (ID vacío o texto no numérico).
5. **Opción 3** mostrando lista de correos.
6. **Manejo de error** (por ejemplo, ID inexistente como 9999).

> Consejo: antes de cada captura limpiar resultados para que la evidencia sea clara y ordenada.
