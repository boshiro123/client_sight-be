import { DataTypes, Model, Optional } from "sequelize"
import sequelize from "../config/database"
import { TourType } from "./tour.model"

// Пол
export enum Gender {
  MALE = "MALE",
  FEMALE = "FEMALE",
  OTHER = "OTHER",
}

// Возрастные категории
export enum AgeGroup {
  UNDER_18 = "UNDER_18", // до 18
  AGE_18_20 = "AGE_18_20", // 18-20
  AGE_21_25 = "AGE_21_25", // 21-25
  AGE_26_35 = "AGE_26_35", // 26-35
  AGE_36_50 = "AGE_36_50", // 36-50
  OVER_50 = "OVER_50", // 50+
}

// Интерфейс атрибутов контакта
export interface ContactAttributes {
  id: number
  fullName: string
  phoneNumber: string
  email: string
  ageGroup: AgeGroup
  gender: Gender
  preferredTourType?: TourType | string
  discountPercent: number
  additionalInfo?: string
  isClient: boolean // Признак, что контакт является клиентом
  userId?: number // Связь с аккаунтом пользователя, если есть
  createdAt?: Date
  updatedAt?: Date
}

// Интерфейс для создания контакта
export interface ContactCreationAttributes
  extends Optional<
    ContactAttributes,
    "id" | "discountPercent" | "isClient" | "userId" | "createdAt" | "updatedAt"
  > {}

// Класс модели контакта
class Contact
  extends Model<ContactAttributes, ContactCreationAttributes>
  implements ContactAttributes
{
  public id!: number
  public fullName!: string
  public phoneNumber!: string
  public email!: string
  public ageGroup!: AgeGroup
  public gender!: Gender
  public preferredTourType?: TourType | string
  public discountPercent!: number
  public additionalInfo?: string
  public isClient!: boolean
  public userId?: number

  // Временные метки
  public readonly createdAt!: Date
  public readonly updatedAt!: Date
}

// Инициализация модели
Contact.init(
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
    ageGroup: {
      type: DataTypes.ENUM(...Object.values(AgeGroup)),
      allowNull: false,
    },
    gender: {
      type: DataTypes.ENUM(...Object.values(Gender)),
      allowNull: false,
    },
    preferredTourType: {
      type: DataTypes.STRING,
      allowNull: true,
    },
    discountPercent: {
      type: DataTypes.INTEGER,
      allowNull: false,
      defaultValue: 0,
      validate: {
        min: 0,
        max: 100,
      },
    },
    additionalInfo: {
      type: DataTypes.TEXT,
      allowNull: true,
    },
    isClient: {
      type: DataTypes.BOOLEAN,
      allowNull: false,
      defaultValue: false,
    },
    userId: {
      type: DataTypes.INTEGER,
      allowNull: true,
      references: {
        model: "users",
        key: "id",
      },
    },
  },
  {
    sequelize,
    tableName: "contacts",
    modelName: "Contact",
  }
)

export default Contact
