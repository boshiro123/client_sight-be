import { initDatabase } from "../config/init-db"

// Запуск инициализации
initDatabase()
  .then(() => {
    console.log("Инициализация базы данных завершена успешно")
    process.exit(0)
  })
  .catch(error => {
    console.error("Ошибка при инициализации базы данных:", error)
    process.exit(1)
  })
