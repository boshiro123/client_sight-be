import { DataTypes, Model, Optional } from "sequelize"
import sequelize from "../config/database"
import Tour from "./tour.model"
import User from "./user.model"

// Статус заявки
export enum ApplicationStatus {
  PENDING = "PENDING", // В ожидании
  APPROVED = "APPROVED", // Подтверждена
  REJECTED = "REJECTED", // Отклонена
  CANCELLED = "CANCELLED", // Отменена
}

// Интерфейс атрибутов заявки
export interface ApplicationAttributes {
  id: number
  fullName: string
  phoneNumber: string
  email: string
  tourId: number
  userId?: number // Может быть null, если заявка от незарегистрированного пользователя
  status: ApplicationStatus
  createdAt?: Date
  updatedAt?: Date
}

// Интерфейс для создания заявки
export interface ApplicationCreationAttributes
  extends Optional<
    ApplicationAttributes,
    "id" | "userId" | "status" | "createdAt" | "updatedAt"
  > {}

// Класс модели заявки
class Application
  extends Model<ApplicationAttributes, ApplicationCreationAttributes>
  implements ApplicationAttributes
{
  public id!: number
  public fullName!: string
  public phoneNumber!: string
  public email!: string
  public tourId!: number
  public userId?: number
  public status!: ApplicationStatus

  // Временные метки
  public readonly createdAt!: Date
  public readonly updatedAt!: Date

  // Ассоциации
  public tour?: Tour
  public user?: User
}

// Инициализация модели
Application.init(
  {
    id: {
      type: DataTypes.INTEGER,
      autoIncrement: true,
      primaryKey: true,
    },
    fullName: {
      type: DataTypes.STRING,
      allowNull: false,
    },
    phoneNumber: {
      type: DataTypes.STRING,
      allowNull: false,
    },
    email: {
      type: DataTypes.STRING,
      allowNull: false,
      validate: {
        isEmail: true,
      },
    },
    tourId: {
      type: DataTypes.INTEGER,
      allowNull: false,
      references: {
        model: "tours",
        key: "id",
      },
    },
    userId: {
      type: DataTypes.INTEGER,
      allowNull: true,
      references: {
        model: "users",
        key: "id",
      },
    },
    status: {
      type: DataTypes.ENUM(...Object.values(ApplicationStatus)),
      allowNull: false,
      defaultValue: ApplicationStatus.PENDING,
    },
  },
  {
    sequelize,
    tableName: "applications",
    modelName: "Application",
  }
)

// Определение ассоциаций будет добавлено в файле ассоциаций

export default Application
