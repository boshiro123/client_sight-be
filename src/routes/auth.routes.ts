import { Router } from "express"
import { body } from "express-validator"
import authController from "../controllers/auth.controller"
import authMiddleware from "../middleware/auth.middleware"
import validationMiddleware from "../middleware/validation.middleware"

const router = Router()

// Регистрация нового пользователя
router.post(
  "/register",
  [
    body("username")
      .trim()
      .isLength({ min: 3, max: 20 })
      .withMessage("Имя пользователя должно быть от 3 до 20 символов"),
    body("email").isEmail().withMessage("Введите корректный email"),
    body("password")
      .isLength({ min: 6 })
      .withMessage("Пароль должен быть минимум 6 символов"),
    validationMiddleware,
  ],
  authController.register
)

// Авторизация пользователя
router.post(
  "/login",
  [
    body("email").isEmail().withMessage("Введите корректный email"),
    body("password").exists().withMessage("Введите пароль"),
    validationMiddleware,
  ],
  authController.login
)

// Проверка авторизации
router.get("/check", authMiddleware, authController.check)

export default router
