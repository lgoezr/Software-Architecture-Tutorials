const { createApp } = require('./app')

// Define el puerto, tomando la variable de entorno PORT o usando 3000 por defecto
const PORT = Number(process.env.PORT) || 3000

// Crea la instancia de la aplicación Express
const app = createApp()

// Inicia el servidor
app.listen(PORT, () => {
  console.log(`Servicio Random Quotes escuchando en el puerto ${PORT}`)
})