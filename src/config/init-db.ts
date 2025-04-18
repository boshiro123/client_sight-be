import sequelize from "./database"
import User, { UserRole } from "../models/user.model"
import Tour, { Season, TourType } from "../models/tour.model"
import bcrypt from "bcrypt"

// Функция инициализации базы данных
export async function initDatabase() {
  try {
    // Синхронизация моделей с базой данных (создание таблиц)
    await sequelize.sync({ force: true })
    console.log("База данных синхронизирована")

    // Создание тестовых пользователей
    await createInitialUsers()

    // Создание тестовых туров
    await createInitialTours()

    console.log("Начальные данные успешно добавлены")
  } catch (error) {
    console.error("Ошибка при инициализации базы данных:", error)
  }
}

// Создание начальных пользователей
async function createInitialUsers() {
  // Создание менеджера
  const salt = await bcrypt.genSalt(10)
  const managerPassword = await bcrypt.hash("admin123", salt)

  await User.create({
    username: "manager",
    email: "manager@example.com",
    password: managerPassword,
    role: UserRole.MANAGER,
  })

  // Создание сотрудника
  const employeePassword = await bcrypt.hash("employee123", salt)

  await User.create({
    username: "employee",
    email: "employee@example.com",
    password: employeePassword,
    role: UserRole.EMPLOYEE,
  })

  // Создание туриста
  const touristPassword = await bcrypt.hash("tourist123", salt)

  await User.create({
    username: "tourist",
    email: "tourist@example.com",
    password: touristPassword,
    role: UserRole.TOURIST,
  })
}

// Создание начальных туров
async function createInitialTours() {
  const tours = [
    {
      name: "Пляжный отдых в Турции",
      description:
        'Прекрасный отдых на пляжах Турции с системой "все включено"',
      country: "Турция",
      season: Season.SUMMER,
      type: TourType.BEACH,
      startDate: new Date(2023, 5, 15), // 15 июня 2023
      endDate: new Date(2023, 5, 25), // 25 июня 2023
      totalSlots: 30,
      availableSlots: 10,
      isRegistrationClosed: false,
    },
    {
      name: "Горнолыжный курорт в Альпах",
      description: "Активный отдых на лучших горнолыжных курортах Альп",
      country: "Швейцария",
      season: Season.WINTER,
      type: TourType.ACTIVE,
      startDate: new Date(2023, 0, 10), // 10 января 2023
      endDate: new Date(2023, 0, 20), // 20 января 2023
      totalSlots: 20,
      availableSlots: 5,
      isRegistrationClosed: false,
    },
    {
      name: "Семейный отдых в Греции",
      description: "Идеальный семейный отдых на побережье Греции",
      country: "Греция",
      season: Season.SUMMER,
      type: TourType.FAMILY,
      startDate: new Date(2023, 6, 1), // 1 июля 2023
      endDate: new Date(2023, 6, 14), // 14 июля 2023
      totalSlots: 25,
      availableSlots: 15,
      isRegistrationClosed: false,
    },
    {
      name: "Экстремальный поход в Непал",
      description: "Восхождение к базовому лагерю Эвереста",
      country: "Непал",
      season: Season.SPRING,
      type: TourType.EXTREME,
      startDate: new Date(2023, 3, 5), // 5 апреля 2023
      endDate: new Date(2023, 3, 20), // 20 апреля 2023
      totalSlots: 15,
      availableSlots: 3,
      isRegistrationClosed: false,
    },
    {
      name: "Осенний тур по Японии",
      description: "Путешествие по Японии в сезон красных клёнов",
      country: "Япония",
      season: Season.AUTUMN,
      type: TourType.FAMILY,
      startDate: new Date(2023, 9, 10), // 10 октября 2023
      endDate: new Date(2023, 9, 24), // 24 октября 2023
      totalSlots: 20,
      availableSlots: 20,
      isRegistrationClosed: false,
    },
  ]

  for (const tour of tours) {
    await Tour.create(tour)
  }
}

// Запуск инициализации, если скрипт запущен напрямую
if (require.main === module) {
  initDatabase()
    .then(() => {
      console.log("Инициализация завершена")
      process.exit(0)
    })
    .catch(error => {
      console.error("Ошибка при инициализации:", error)
      process.exit(1)
    })
}
