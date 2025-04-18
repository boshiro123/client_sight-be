import { Request, Response } from "express"
import analyticsService from "../services/analytics.service"

class AnalyticsController {
  // Получение данных для анализа (без предсказательной аналитики)
  async getAnalysisData(req: Request, res: Response) {
    try {
      const [
        seasonDistribution,
        typeDistribution,
        genderDistribution,
        ageGroupDistribution,
        clientContactRatio,
        regularClientPercentage,
      ] = await Promise.all([
        analyticsService.getTourSeasonDistribution(),
        analyticsService.getTourTypeDistribution(),
        analyticsService.getGenderDistribution(),
        analyticsService.getAgeGroupDistribution(),
        analyticsService.getClientContactRatio(),
        analyticsService.getRegularClientPercentage(),
      ])

      res.json({
        seasonDistribution,
        typeDistribution,
        genderDistribution,
        ageGroupDistribution,
        clientContactRatio,
        regularClientPercentage,
      })
    } catch (error) {
      console.error("Ошибка при получении данных анализа:", error)
      res
        .status(500)
        .json({ message: "Ошибка сервера при получении данных анализа" })
    }
  }

  // Получение данных для аналитики (с предсказательной аналитикой)
  async getPredictiveAnalytics(req: Request, res: Response) {
    try {
      const [
        applicationsBySeasons,
        ageGroupByTourType,
        tourTypePopularityTrends,
      ] = await Promise.all([
        analyticsService.getApplicationsBySeasons(),
        analyticsService.getAgeGroupByTourType(),
        analyticsService.predictTourTypePopularity(),
      ])

      res.json({
        applicationsBySeasons,
        ageGroupByTourType,
        tourTypePopularityTrends,
      })
    } catch (error) {
      console.error("Ошибка при получении предсказательной аналитики:", error)
      res
        .status(500)
        .json({
          message: "Ошибка сервера при получении предсказательной аналитики",
        })
    }
  }

  // Формирование полного отчёта
  async generateFullReport(req: Request, res: Response) {
    try {
      const analysisData = await this.getAnalysisDataForReport()
      const predictiveData = await this.getPredictiveDataForReport()

      res.json({
        timestamp: new Date(),
        analysisData,
        predictiveData,
      })
    } catch (error) {
      console.error("Ошибка при формировании полного отчёта:", error)
      res
        .status(500)
        .json({ message: "Ошибка сервера при формировании полного отчёта" })
    }
  }

  // Вспомогательные методы для формирования отчёта
  private async getAnalysisDataForReport() {
    const [
      seasonDistribution,
      typeDistribution,
      genderDistribution,
      ageGroupDistribution,
      clientContactRatio,
      regularClientPercentage,
    ] = await Promise.all([
      analyticsService.getTourSeasonDistribution(),
      analyticsService.getTourTypeDistribution(),
      analyticsService.getGenderDistribution(),
      analyticsService.getAgeGroupDistribution(),
      analyticsService.getClientContactRatio(),
      analyticsService.getRegularClientPercentage(),
    ])

    return {
      seasonDistribution,
      typeDistribution,
      genderDistribution,
      ageGroupDistribution,
      clientContactRatio,
      regularClientPercentage,
    }
  }

  private async getPredictiveDataForReport() {
    const [
      applicationsBySeasons,
      ageGroupByTourType,
      tourTypePopularityTrends,
    ] = await Promise.all([
      analyticsService.getApplicationsBySeasons(),
      analyticsService.getAgeGroupByTourType(),
      analyticsService.predictTourTypePopularity(),
    ])

    return {
      applicationsBySeasons,
      ageGroupByTourType,
      tourTypePopularityTrends,
    }
  }
}

export default new AnalyticsController()
