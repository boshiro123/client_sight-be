import { Request, Response, NextFunction } from "express"
import { UserRole } from "../models/user.model"

// Middleware для проверки роли пользователя
export default function (roles: UserRole[]) {
  return function (req: Request, res: Response, next: NextFunction) {
    if (req.method === "OPTIONS") {
      return next()
    }

    try {
      const user = req.user

      if (!user) {
        return res.status(401).json({ message: "Не авторизован" })
      }

      const hasRole = roles.includes(user.role as UserRole)

      if (!hasRole) {
        return res.status(403).json({ message: "Нет доступа" })
      }

      next()
    } catch (error) {
      console.error("Ошибка в middleware проверки роли:", error)
      return res.status(500).json({ message: "Ошибка сервера" })
    }
  }
}
