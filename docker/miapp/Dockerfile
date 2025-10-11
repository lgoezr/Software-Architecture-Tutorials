FROM python:3.11-slim

WORKDIR /app

# copiar e instalar dependencias
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# copiar el resto
COPY . .

EXPOSE 8000

# comando para ejecutar la app
CMD ["python", "app.py"]
