import { Router } from "express"
import analyticsController from "../controllers/analytics.controller"
import authMiddleware from "../middleware/auth.middleware"
import roleMiddleware from "../middleware/role.middleware"
import { UserRole } from "../models/user.model"

const router = Router()

// Доступ только для руководителей
router.get(
  "/analysis",
  authMiddleware,
  roleMiddleware([UserRole.MANAGER]),
  analyticsController.getAnalysisData
)

router.get(
  "/predictive",
  authMiddleware,
  roleMiddleware([UserRole.MANAGER]),
  analyticsController.getPredictiveAnalytics
)

router.get(
  "/report",
  authMiddleware,
  roleMiddleware([UserRole.MANAGER]),
  analyticsController.generateFullReport
)

export default router
