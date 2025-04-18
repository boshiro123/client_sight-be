import { DataTypes, Model, Optional } from "sequelize"
import sequelize from "../config/database"

// Интерфейс атрибутов связи клиент-тур
export interface ClientTourAttributes {
  id: number
  contactId: number
  tourId: number
  applicationId?: number // Связь с заявкой, если была
  isActive: boolean // Активный тур (текущий или будущий)
  createdAt?: Date
  updatedAt?: Date
}

// Интерфейс для создания связи клиент-тур
export interface ClientTourCreationAttributes
  extends Optional<
    ClientTourAttributes,
    "id" | "applicationId" | "isActive" | "createdAt" | "updatedAt"
  > {}

// Класс модели связи клиент-тур
class ClientTour
  extends Model<ClientTourAttributes, ClientTourCreationAttributes>
  implements ClientTourAttributes
{
  public id!: number
  public contactId!: number
  public tourId!: number
  public applicationId?: number
  public isActive!: boolean

  // Временные метки
  public readonly createdAt!: Date
  public readonly updatedAt!: Date
}

// Инициализация модели
ClientTour.init(
  {
    id: {
      type: DataTypes.INTEGER,
      autoIncrement: true,
      primaryKey: true,
    },
    contactId: {
      type: DataTypes.INTEGER,
      allowNull: false,
      references: {
        model: "contacts",
        key: "id",
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
    applicationId: {
      type: DataTypes.INTEGER,
      allowNull: true,
      references: {
        model: "applications",
        key: "id",
      },
    },
    isActive: {
      type: DataTypes.BOOLEAN,
      allowNull: false,
      defaultValue: true,
    },
  },
  {
    sequelize,
    tableName: "client_tours",
    modelName: "ClientTour",
    indexes: [
      {
        unique: true,
        fields: ["contactId", "tourId"],
      },
    ],
  }
)

export default ClientTour
