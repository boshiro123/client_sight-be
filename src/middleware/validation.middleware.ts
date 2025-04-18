import { Request, Response, NextFunction } from "express"
import { validationResult } from "express-validator"

// Middleware для проверки ошибок валидации
export default function (req: Request, res: Response, next: NextFunction) {
  const errors = validationResult(req)

  if (!errors.isEmpty()) {
    return res.status(400).json({
      errors: errors.array(),
      message: "Ошибка валидации данных",
    })
  }

  next()
}
