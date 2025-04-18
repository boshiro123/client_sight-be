import express from "express"
import cors from "cors"
import dotenv from "dotenv"
import path from "path"
import sequelize from "./config/database"

// Импорт моделей и ассоциаций
import "./models"

// Импорт роутеров
import authRoutes from "./routes/auth.routes"
import userRoutes from "./routes/user.routes"
import tourRoutes from "./routes/tour.routes"
import applicationRoutes from "./routes/application.routes"
import contactRoutes from "./routes/contact.routes"
import clientRoutes from "./routes/client.routes"
import analyticsRoutes from "./routes/analytics.routes"

// Загрузка переменных окружения
dotenv.config()

const app = express()
const PORT = process.env.PORT || 5000

// Middleware
app.use(cors())
app.use(express.json())
app.use(express.urlencoded({ extended: true }))

// Статические файлы
app.use("/uploads", express.static(path.join(__dirname, "../uploads")))

// Маршруты API
app.use("/api/auth", authRoutes)
app.use("/api/users", userRoutes)
app.use("/api/tours", tourRoutes)
app.use("/api/applications", applicationRoutes)
app.use("/api/contacts", contactRoutes)
app.use("/api/clients", clientRoutes)
app.use("/api/analytics", analyticsRoutes)

// Подключение к PostgreSQL и запуск сервера
const startServer = async () => {
  try {
    // Синхронизация с базой данных (в production использовать migrate вместо sync)
    await sequelize.sync({
      force:
        process.env.NODE_ENV === "development" &&
        process.env.DB_RESET === "true",
    })
    console.log("База данных синхронизирована")

    app.listen(PORT, () => {
      console.log(`Сервер запущен на порту ${PORT}`)
    })
  } catch (error) {
    console.error("Ошибка подключения к базе данных:", error)
  }
}

startServer()

// Обработка ошибок
app.use(
  (
    err: any,
    req: express.Request,
    res: express.Response,
    next: express.NextFunction
  ) => {
    console.error(err.stack)
    res.status(500).json({
      message: "Произошла ошибка на сервере",
      error: process.env.NODE_ENV === "development" ? err.message : {},
    })
  }
)
