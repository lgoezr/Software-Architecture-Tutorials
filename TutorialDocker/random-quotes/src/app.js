const express = require('express')
const { quotes, getRandomQuote } = require('./quotes')

function createApp () {
  const app = express()

  // Middleware para parsear el cuerpo de las peticiones JSON
  app.use(express.json())

  // Ruta principal - Documentación / Bienvenida
  app.get('/', (req, res) => {
    res.json({
      status: 'ok',
      message: 'Welcome to the Random Quotes API',
      endpoints: {
        random: '/quotes/random',
        all: '/quotes',
        byId: '/quotes/:id'
      }
    })
  })

  // Ruta para obtener todas las citas
  app.get('/quotes', (req, res) => {
    res.json({
      count: quotes.length,
      data: quotes
    })
  })

  // Ruta para obtener una cita aleatoria
  app.get('/quotes/random', (req, res) => {
    res.json(getRandomQuote())
  })

  // Ruta para obtener una cita por ID
  app.get('/quotes/:id', (req, res) => {
    // Convierte el parámetro ID de string a número
    const id = Number(req.params.id)

    // Busca la cita que coincida con el ID
    const quote = quotes.find(entry => entry.id === id)

    // Si la cita no se encuentra, retorna 404
    if (!quote) {
      return res.status(404).json({
        error: `Quote with id ${id} not found`
      })
    }

    // Si la cita se encuentra, la retorna
    return res.json(quote)
  })

  return app
}

// Exporta la función para crear la aplicación Express
module.exports = { createApp }