import { Op, Sequelize } from "sequelize"
import { Tour, Contact, Application, ClientTour } from "../models"
import { TourType, Season } from "../models/tour.model"
import { Gender, AgeGroup } from "../models/contact.model"

// Интерфейс для данных распределения
interface DistributionData {
  [key: string]: number
}

// Интерфейс для данных кросс-распределения (например, возраст x тип тура)
interface CrossDistributionData {
  [key: string]: {
    [key: string]: number
  }
}

// Класс сервиса аналитики
class AnalyticsService {
  // Получение распределения туров по сезонам
  async getTourSeasonDistribution(): Promise<DistributionData> {
    const seasons = await Tour.findAll({
      attributes: [
        "season",
        [Sequelize.fn("COUNT", Sequelize.col("id")), "count"],
      ],
      group: ["season"],
      raw: true,
    })

    const result: DistributionData = {}
    seasons.forEach((item: any) => {
      result[item.season] = parseInt(item.count, 10)
    })

    return result
  }

  // Получение распределения туров по типам
  async getTourTypeDistribution(): Promise<DistributionData> {
    const types = await Tour.findAll({
      attributes: [
        "type",
        [Sequelize.fn("COUNT", Sequelize.col("id")), "count"],
      ],
      group: ["type"],
      raw: true,
    })

    const result: DistributionData = {}
    types.forEach((item: any) => {
      result[item.type] = parseInt(item.count, 10)
    })

    return result
  }

  // Получение распределения контактов по полу
  async getGenderDistribution(): Promise<DistributionData> {
    const genders = await Contact.findAll({
      attributes: [
        "gender",
        [Sequelize.fn("COUNT", Sequelize.col("id")), "count"],
      ],
      group: ["gender"],
      raw: true,
    })

    const result: DistributionData = {}
    genders.forEach((item: any) => {
      result[item.gender] = parseInt(item.count, 10)
    })

    return result
  }

  // Получение распределения контактов по возрасту
  async getAgeGroupDistribution(): Promise<DistributionData> {
    const ageGroups = await Contact.findAll({
      attributes: [
        "ageGroup",
        [Sequelize.fn("COUNT", Sequelize.col("id")), "count"],
      ],
      group: ["ageGroup"],
      raw: true,
    })

    const result: DistributionData = {}
    ageGroups.forEach((item: any) => {
      result[item.ageGroup] = parseInt(item.count, 10)
    })

    return result
  }

  // Получение соотношения клиентов и контактов
  async getClientContactRatio(): Promise<{
    clients: number
    contacts: number
    ratio: number
  }> {
    const clientCount = await Contact.count({
      where: { isClient: true },
    })

    const contactCount = await Contact.count({
      where: { isClient: false },
    })

    return {
      clients: clientCount,
      contacts: contactCount,
      ratio: contactCount > 0 ? clientCount / contactCount : 0,
    }
  }

  // Получение процента постоянных клиентов (3+ туров)
  async getRegularClientPercentage(): Promise<{
    regular: number
    total: number
    percentage: number
  }> {
    // Подсчёт клиентов с 3+ турами
    const contactsWithMultipleTours = await Contact.findAll({
      attributes: ["id"],
      include: [
        {
          model: ClientTour,
          as: "clientTours",
          attributes: [],
        },
      ],
      group: ["Contact.id"],
      having: Sequelize.literal("COUNT(clientTours.id) >= 3"),
      raw: true,
    })

    const regularCount = contactsWithMultipleTours.length
    const totalClientCount = await Contact.count({
      where: { isClient: true },
    })

    return {
      regular: regularCount,
      total: totalClientCount,
      percentage:
        totalClientCount > 0 ? (regularCount / totalClientCount) * 100 : 0,
    }
  }

  // Анализ заявок по сезонам
  async getApplicationsBySeasons(): Promise<DistributionData> {
    const applications = await Application.findAll({
      attributes: [
        [Sequelize.literal('"tour"."season"'), "season"],
        [Sequelize.fn("COUNT", Sequelize.col("Application.id")), "count"],
      ],
      include: [
        {
          model: Tour,
          as: "tour",
          attributes: [],
        },
      ],
      group: ['"tour"."season"'],
      raw: true,
    })

    const result: DistributionData = {}
    applications.forEach((item: any) => {
      result[item.season] = parseInt(item.count, 10)
    })

    return result
  }

  // Кросс-анализ возраста и типа тура
  async getAgeGroupByTourType(): Promise<CrossDistributionData> {
    const contacts = await Contact.findAll({
      attributes: [
        "ageGroup",
        "preferredTourType",
        [Sequelize.fn("COUNT", Sequelize.col("id")), "count"],
      ],
      where: {
        preferredTourType: {
          [Op.ne]: null,
        },
      },
      group: ["ageGroup", "preferredTourType"],
      raw: true,
    })

    const result: CrossDistributionData = {}

    // Инициализация структуры данных
    Object.values(AgeGroup).forEach(ageGroup => {
      result[ageGroup] = {}
      Object.values(TourType).forEach(tourType => {
        result[ageGroup][tourType] = 0
      })
    })

    // Заполнение данными
    contacts.forEach((item: any) => {
      if (result[item.ageGroup] && item.preferredTourType) {
        result[item.ageGroup][item.preferredTourType] = parseInt(item.count, 10)
      }
    })

    return result
  }

  // Предсказательная аналитика: тренды популярности типов туров
  async predictTourTypePopularity(): Promise<any> {
    // Получение истории по типам туров в разные периоды
    const historicalData = await ClientTour.findAll({
      attributes: [
        [
          Sequelize.fn(
            "date_trunc",
            "month",
            Sequelize.col("ClientTour.createdAt")
          ),
          "month",
        ],
        [Sequelize.literal('"tour"."type"'), "tourType"],
        [Sequelize.fn("COUNT", Sequelize.col("ClientTour.id")), "count"],
      ],
      include: [
        {
          model: Tour,
          as: "tour",
          attributes: [],
        },
      ],
      group: ["month", '"tour"."type"'],
      order: [[Sequelize.literal("month"), "ASC"]],
      raw: true,
    })

    // Преобразуем данные в подходящий для анализа формат
    // В реальном проекте здесь был бы более сложный алгоритм ML для предсказания
    const trends: any = {}

    historicalData.forEach((item: any) => {
      if (!trends[item.tourType]) {
        trends[item.tourType] = []
      }

      trends[item.tourType].push({
        month: new Date(item.month),
        count: parseInt(item.count, 10),
      })
    })

    // Простое линейное предсказание на следующие 3 месяца
    Object.keys(trends).forEach(type => {
      const data = trends[type]
      if (data.length >= 2) {
        const lastMonths = data.slice(-3)
        const avgGrowth =
          lastMonths.length >= 2
            ? (lastMonths[lastMonths.length - 1].count - lastMonths[0].count) /
              (lastMonths.length - 1)
            : 0

        const lastMonth = new Date(data[data.length - 1].month)
        const lastCount = data[data.length - 1].count

        for (let i = 1; i <= 3; i++) {
          const nextMonth = new Date(lastMonth)
          nextMonth.setMonth(nextMonth.getMonth() + i)

          trends[type].push({
            month: nextMonth,
            count: Math.max(0, Math.round(lastCount + avgGrowth * i)),
            predicted: true,
          })
        }
      }
    })

    return trends
  }
}

export default new AnalyticsService()
