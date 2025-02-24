# 🚀 MundoCode - Gestión Financiera y Facturación 📊

### Aplicación de gestión financiera y facturación con soporte offline y sincronización en la nube.

## 📌 **Descripción**
**MoneyFlow** es una aplicación diseñada para gestionar ingresos, gastos, clientes y facturación de manera eficiente. Cuenta con almacenamiento **offline** en **Room** y sincronización en **Firestore**, permitiendo trabajar sin conexión y actualizar datos en la nube cuando haya internet disponible.


## 🌟 **Características Principales**
✅ **Gestión Financiera**: Registra ingresos, gastos y transacciones con categorías dinámicas.  
✅ **Facturación Electrónica**: Genera facturas en PDF y permite enviarlas por correo.  
✅ **Escaneo de Facturas**: Usa OCR para extraer datos de facturas físicas.  
✅ **Clientes & Proyectos**: Administra clientes y proyectos con seguimiento.  
✅ **Modo Offline**: Guarda datos en **Room** y los sincroniza con **Firestore** cuando hay internet.  
✅ **Exportación**: Genera reportes en **PDF y Excel** con las transacciones.  
✅ **Interfaz Moderna**: Diseñada con **Jetpack Compose** y un diseño optimizado para móviles y tablets.  
✅ **Seguridad**: Protege las operaciones con autenticación **FirebaseAuth**.


## 📸 **Capturas de Pantalla**
📷 

## 🚀 **Tecnologías Utilizadas**
🔹 **Lenguaje**: Kotlin  
🔹 **Framework UI**: Jetpack Compose  
🔹 **Base de Datos Offline**: Room  
🔹 **Sincronización en la Nube**: Firebase Firestore  
🔹 **Autenticación**: FirebaseAuth (email/contraseña)
🔹 **OCR & Escaneo**: ML Kit (Text Recognition)  
🔹 **Reportes**: PDF (iText) y Excel (Apache POI)  
🔹 **Gestión de Archivos**: FileProvider


## 🔧 **Configuración del Proyecto**

### 1️⃣ **Clonar el Repositorio**
```bash
git clone https://github.com/tu-usuario/mundocode.git
cd mundocode
```

### 2️⃣ **Configurar Firebase**
1. **Crear un Proyecto en Firebase**
2. **Habilitar Firestore y Authentication**
3. **Descargar el `google-services.json` y colocarlo en `app/src/main/`**

### 3️⃣ **Ejecutar la Aplicación**
Asegúrate de tener el entorno de desarrollo listo:
- **Android Studio Arctic Fox (o superior)**
- **Gradle 8+**
- **Dispositivo virtual/emulador con API 30+**

Luego, ejecuta el siguiente comando en Android Studio:
```bash
./gradlew assembleDebug
```

---

## 🚀 **Despliegue y Producción**
Para generar un APK firmado:
1. **Abre el archivo `gradle.properties`** y define la configuración de firma.
2. **Ejecuta el siguiente comando:**
```bash
./gradlew assembleRelease
```
3. **Sube el APK a Google Play Console.**

---

## 👨‍💻 **Contribuir**
Si deseas mejorar **MundoCode**, sigue estos pasos:

1. **Haz un fork del proyecto**
2. **Crea una nueva rama** (`git checkout -b feature/nueva-funcionalidad`)
3. **Realiza tus cambios y sube el código** (`git commit -m "Agregada nueva funcionalidad"`)
4. **Abre un Pull Request**

---

## 📄 **Licencia**
Este proyecto está bajo la **Licencia MIT**. Puedes usarlo y modificarlo libremente.

---

## 📩 **Contacto**
📧 Email: [juanppdev@gmail.com](mailto:juanppdev@gmail.com)  
📢 LinkedIn: [linkedin.com/in/juanppdev](https://linkedin.com/in/juanppdev)
