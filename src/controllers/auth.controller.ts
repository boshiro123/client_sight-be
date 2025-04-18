import { Request, Response } from "express"
import jwt from "jsonwebtoken"
import bcrypt from "bcrypt"
import { User, UserRole } from "../models/user.model"

class AuthController {
  // Регистрация пользователя (только для туристов)
  async register(req: Request, res: Response) {
    try {
      const { username, email, password } = req.body

      // Проверка, что email и username еще не используются
      const existingEmail = await User.findOne({ where: { email } })
      if (existingEmail) {
        return res
          .status(400)
          .json({ message: "Пользователь с таким email уже существует" })
      }

      const existingUsername = await User.findOne({ where: { username } })
      if (existingUsername) {
        return res
          .status(400)
          .json({ message: "Пользователь с таким именем уже существует" })
      }

      // Создание нового пользователя-туриста
      const user = await User.create({
        username,
        email,
        password,
        role: UserRole.TOURIST,
      })

      // Генерация JWT токена
      const token = this.generateToken(user)

      res.status(201).json({
        token,
        user: {
          id: user.id,
          username: user.username,
          email: user.email,
          role: user.role,
        },
      })
    } catch (error) {
      console.error("Ошибка при регистрации:", error)
      res.status(500).json({ message: "Ошибка при регистрации пользователя" })
    }
  }

  // Авторизация пользователя
  async login(req: Request, res: Response) {
    try {
      const { email, password } = req.body

      // Поиск пользователя по email
      const user = await User.findOne({ where: { email } })
      if (!user) {
        return res.status(404).json({ message: "Пользователь не найден" })
      }

      // Проверка пароля
      const isPasswordValid = await user.comparePassword(password)
      if (!isPasswordValid) {
        return res.status(400).json({ message: "Неверный пароль" })
      }

      // Генерация JWT токена
      const token = this.generateToken(user)

      res.json({
        token,
        user: {
          id: user.id,
          username: user.username,
          email: user.email,
          role: user.role,
        },
      })
    } catch (error) {
      console.error("Ошибка при входе:", error)
      res.status(500).json({ message: "Ошибка при входе в систему" })
    }
  }

  // Проверка токена и получение данных пользователя
  async check(req: Request, res: Response) {
    try {
      const user = req.user

      // Возвращаем обновленный токен и информацию о пользователе
      const token = this.generateToken(user)

      res.json({
        token,
        user: {
          id: user.id,
          username: user.username,
          email: user.email,
          role: user.role,
        },
      })
    } catch (error) {
      console.error("Ошибка при проверке пользователя:", error)
      res.status(500).json({ message: "Ошибка при проверке пользователя" })
    }
  }

  // Метод для генерации JWT токена
  private generateToken(user: User) {
    const payload = {
      id: user.id,
      email: user.email,
      role: user.role,
    }

    return jwt.sign(payload, process.env.JWT_SECRET || "secret", {
      expiresIn: "24h",
    })
  }
}

export default new AuthController()
