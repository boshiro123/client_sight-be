import { DataTypes, Model, Optional } from "sequelize"
import sequelize from "../config/database"
import bcrypt from "bcrypt"

// Определение типа роли пользователя
export enum UserRole {
  TOURIST = "TOURIST",
  EMPLOYEE = "EMPLOYEE",
  MANAGER = "MANAGER",
}

// Интерфейс для атрибутов пользователя
export interface UserAttributes {
  id: number
  username: string
  email: string
  password: string
  role: UserRole
  createdAt?: Date
  updatedAt?: Date
}

// Интерфейс для создания пользователя
export interface UserCreationAttributes
  extends Optional<UserAttributes, "id" | "createdAt" | "updatedAt"> {}

// Класс модели пользователя
class User
  extends Model<UserAttributes, UserCreationAttributes>
  implements UserAttributes
{
  public id!: number
  public username!: string
  public email!: string
  public password!: string
  public role!: UserRole

  // Временные метки
  public readonly createdAt!: Date
  public readonly updatedAt!: Date

  // Метод для проверки пароля
  public async comparePassword(password: string): Promise<boolean> {
    return bcrypt.compare(password, this.password)
  }
}

// Инициализация модели
User.init(
  {
    id: {
      type: DataTypes.INTEGER,
      autoIncrement: true,
      primaryKey: true,
    },
    username: {
      type: DataTypes.STRING,
      allowNull: false,
      unique: true,
    },
    email: {
      type: DataTypes.STRING,
      allowNull: false,
      unique: true,
      validate: {
        isEmail: true,
      },
    },
    password: {
      type: DataTypes.STRING,
      allowNull: false,
    },
    role: {
      type: DataTypes.ENUM(...Object.values(UserRole)),
      allowNull: false,
      defaultValue: UserRole.TOURIST,
    },
  },
  {
    sequelize,
    tableName: "users",
    modelName: "User",
  }
)

// Хук перед сохранением для хеширования пароля
User.beforeCreate(async user => {
  if (user.password) {
    const salt = await bcrypt.genSalt(10)
    user.password = await bcrypt.hash(user.password, salt)
  }
})

User.beforeUpdate(async user => {
  if (user.changed("password")) {
    const salt = await bcrypt.genSalt(10)
    user.password = await bcrypt.hash(user.password, salt)
  }
})

export default User
