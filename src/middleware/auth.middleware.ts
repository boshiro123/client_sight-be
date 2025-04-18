import { Request, Response, NextFunction } from "express"
import jwt from "jsonwebtoken"
import { User } from "../models"

// Расширяем интерфейс Request для добавления пользователя
declare global {
  namespace Express {
    interface Request {
      user?: any
    }
  }
}

// Middleware для проверки JWT токена
export default async function (
  req: Request,
  res: Response,
  next: NextFunction
) {
  if (req.method === "OPTIONS") {
    return next()
  }

  try {
    const token = req.headers.authorization?.split(" ")[1] // Bearer TOKEN

    if (!token) {
      return res.status(401).json({ message: "Не авторизован" })
    }

    const decoded = jwt.verify(token, process.env.JWT_SECRET || "secret") as {
      id: number
    }
    const user = await User.findByPk(decoded.id)

    if (!user) {
      return res.status(401).json({ message: "Пользователь не найден" })
    }

    req.user = user
    next()
  } catch (error) {
    return res.status(401).json({ message: "Не авторизован" })
  }
}
