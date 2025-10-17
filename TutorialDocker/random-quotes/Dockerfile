# ETAPA BASE (Base Stage)
# Utilizamos una imagen oficial de Node.js versión 20 en Alpine Linux (ligera)
FROM node:20-alpine AS base

# Establece el directorio de trabajo dentro del contenedor
WORKDIR /usr/src/app

# Copia los archivos de manifiesto de dependencias
COPY package*.json ./

# Instala las dependencias de producción, omitiendo las de desarrollo
# 'npm ci' garantiza una instalación limpia y reproducible
RUN npm ci --omit=dev

# Copia el resto de los archivos de la aplicación al contenedor
COPY . .

# Define la variable de entorno para indicar que es un ambiente de producción
ENV NODE_ENV=production

# Indica el puerto en el que la aplicación Express estará escuchando
EXPOSE 3000

# Comando para iniciar la aplicación cuando se ejecute el contenedor
CMD ["node", "src/server.js"]