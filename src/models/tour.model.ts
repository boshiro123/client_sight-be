import { DataTypes, Model, Optional } from "sequelize"
import sequelize from "../config/database"

// Сезоны
export enum Season {
  WINTER = "WINTER",
  SPRING = "SPRING",
  SUMMER = "SUMMER",
  AUTUMN = "AUTUMN",
}

// Типы туров
export enum TourType {
  BEACH = "BEACH", // Пляжный
  ACTIVE = "ACTIVE", // Активный
  FAMILY = "FAMILY", // Семейный
  EXTREME = "EXTREME", // Экстремальный
}

// Интерфейс атрибутов тура
export interface TourAttributes {
  id: number
  name: string
  description: string
  country: string
  season: Season
  type: TourType | string // Возможность добавлять свои типы
  imagePath?: string
  filePath?: string
  startDate: Date
  endDate: Date
  duration: number
  totalSlots: number
  availableSlots: number
  isRegistrationClosed: boolean
  createdAt?: Date
  updatedAt?: Date
}

// Интерфейс для создания тура
export interface TourCreationAttributes
  extends Optional<
    TourAttributes,
    "id" | "duration" | "isRegistrationClosed" | "createdAt" | "updatedAt"
  > {}

// Класс модели тура
class Tour
  extends Model<TourAttributes, TourCreationAttributes>
  implements TourAttributes
{
  public id!: number
  public name!: string
  public description!: string
  public country!: string
  public season!: Season
  public type!: TourType | string
  public imagePath?: string
  public filePath?: string
  public startDate!: Date
  public endDate!: Date
  public duration!: number
  public totalSlots!: number
  public availableSlots!: number
  public isRegistrationClosed!: boolean

  // Временные метки
  public readonly createdAt!: Date
  public readonly updatedAt!: Date

  // Метод для проверки, доступен ли тур для записи
  public isAvailable(): boolean {
    const today = new Date()
    const oneDayBefore = new Date(this.startDate)
    oneDayBefore.setDate(oneDayBefore.getDate() - 1)

    return (
      !this.isRegistrationClosed &&
      this.availableSlots > 0 &&
      today <= oneDayBefore
    )
  }
}

// Инициализация модели
Tour.init(
  {
    id: {
      type: DataTypes.INTEGER,
      autoIncrement: true,
      primaryKey: true,
    },
    name: {
      type: DataTypes.STRING,
      allowNull: false,
    },
    description: {
      type: DataTypes.TEXT,
      allowNull: false,
    },
    country: {
      type: DataTypes.STRING,
      allowNull: false,
    },
    season: {
      type: DataTypes.ENUM(...Object.values(Season)),
      allowNull: false,
    },
    type: {
      type: DataTypes.STRING,
      allowNull: false,
    },
    imagePath: {
      type: DataTypes.STRING,
      allowNull: true,
    },
    filePath: {
      type: DataTypes.STRING,
      allowNull: true,
    },
    startDate: {
      type: DataTypes.DATE,
      allowNull: false,
    },
    endDate: {
      type: DataTypes.DATE,
      allowNull: false,
    },
    duration: {
      type: DataTypes.INTEGER,
      allowNull: false,
      defaultValue: 0,
    },
    totalSlots: {
      type: DataTypes.INTEGER,
      allowNull: false,
    },
    availableSlots: {
      type: DataTypes.INTEGER,
      allowNull: false,
    },
    isRegistrationClosed: {
      type: DataTypes.BOOLEAN,
      allowNull: false,
      defaultValue: false,
    },
  },
  {
    sequelize,
    tableName: "tours",
    modelName: "Tour",
    hooks: {
      beforeCreate: (tour: Tour) => {
        // Вычисление продолжительности тура
        const startDate = new Date(tour.startDate)
        const endDate = new Date(tour.endDate)
        const diffTime = Math.abs(endDate.getTime() - startDate.getTime())
        const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
        tour.duration = diffDays + 1 // +1 день, так как включаем день отъезда
      },
      beforeUpdate: (tour: Tour) => {
        if (tour.changed("startDate") || tour.changed("endDate")) {
          const startDate = new Date(tour.startDate)
          const endDate = new Date(tour.endDate)
          const diffTime = Math.abs(endDate.getTime() - startDate.getTime())
          const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
          tour.duration = diffDays + 1
        }
      },
    },
  }
)

export default Tour
