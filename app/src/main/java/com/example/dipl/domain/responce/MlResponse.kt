package com.example.dipl.domain.responce

data class ImageAnalysisResponse(
    val photosCount: Int,
    val apartmentSummary: ApartmentSummary,
    val photos: List<PhotoAnalysis>
)

data class ApartmentSummary(
    val averageVisualScore: Double,
    val visualQualityLevel: String,
    val detectedRooms: List<String>,
    val dominantRepairQuality: String,
    val dominantFurnitureLevel: String,
    val dominantFurnitureCondition: String,
    val priceImpact: PriceImpact,
    val recommendations: List<String>
)

data class PhotoAnalysis(
    val fileName: String,
    val technicalQuality: TechnicalQuality,
    val roomAnalysis: RoomAnalysis,
    val repairAnalysis: RepairAnalysis,
    val furnitureAnalysis: FurnitureAnalysis,
    val interiorAnalysis: InteriorAnalysis,
    val detectedObjects: List<DetectedObject>,
    val visualScore: Double,
    val imageFeatureSize: Int
)

data class TechnicalQuality(
    val brightness: Double,
    val sharpness: Double,
    val photoQualityScore: Int
)

data class RoomAnalysis(
    val roomType: String,
    val confidence: Double
)

data class RepairAnalysis(
    val repairQuality: String,
    val confidence: Double
)

data class FurnitureAnalysis(
    val furnitureCondition: String,
    val furnishingLevel: String,
    val furnitureObjectsCount: Int,
    val confidence: Double
)

data class InteriorAnalysis(
    val style: String,
    val cleanliness: String,
    val styleConfidence: Double,
    val cleanlinessConfidence: Double
)

data class DetectedObject(
    val `object`: String,
    val confidence: Double
)

data class GeoAnalysisResponse(
    val success: Boolean,
    val address: String,
    val coordinates: Coordinates,
    val nearestInfrastructure: NearestInfrastructure,
    val scores: GeoScores,
    val qualityLevel: String,
    val priceImpact: PriceImpact,
    val recommendations: List<String>
)

data class Coordinates(
    val lat: Double,
    val lon: Double
)

data class GeoScores(
    val transportScore: Int,
    val educationScore: Int,
    val healthcareScore: Int,
    val commercialScore: Int,
    val comfortScore: Int,
    val trafficScore: Int,
    val locationScore: Double
)

data class NearestInfrastructure(
    val busStops: List<InfrastructureObject>,
    val tramStops: List<InfrastructureObject>,
    val metroStations: List<InfrastructureObject>,
    val schools: List<InfrastructureObject>,
    val kindergartens: List<InfrastructureObject>,
    val hospitals: List<InfrastructureObject>,
    val clinics: List<InfrastructureObject>,
    val shops: List<InfrastructureObject>,
    val pharmacies: List<InfrastructureObject>,
    val parks: List<InfrastructureObject>,
    val parking: List<InfrastructureObject>
)

data class InfrastructureObject(
    val name: String,
    val distanceMeters: Int,
    val type: String,
    val osmType: String,
    val osmId: Long
)

data class TextAnalysisResponse(
    val success: Boolean,
    val textLength: Int,
    val normalizedTextLength: Int,
    val apartmentFeatures: ApartmentFeatures,
    val repairFeatures: RepairFeatures,
    val furnitureFeatures: FurnitureFeatures,
    val appliancesFeatures: AppliancesFeatures,
    val tenantRules: TenantRules,
    val scores: TextScores,
    val qualityLevel: String,
    val priceImpact: PriceImpact,
    val recommendations: List<String>
)

data class ApartmentFeatures(
    val rooms: Int?,
    val area: Float?,
    val floor: Int?,
    val hasBalcony: Boolean,
    val hasParking: Boolean,
    val hasElevator: Boolean,
    val isStudio: Boolean,
    val hasSeparateRooms: Boolean
)

data class RepairFeatures(
    val repairQuality: String,
    val hasNewRepair: Boolean,
    val hasDesignRepair: Boolean,
    val needsRepair: Boolean
)

data class FurnitureFeatures(
    val hasFurniture: Boolean,
    val noFurniture: Boolean,
    val furnitureCondition: String,
    val hasKitchenFurniture: Boolean,
    val hasWardrobe: Boolean,
    val hasBed: Boolean,
    val hasSofa: Boolean
)

data class AppliancesFeatures(
    val refrigerator: Boolean,
    val washingMachine: Boolean,
    val dishwasher: Boolean,
    val airConditioner: Boolean,
    val tv: Boolean,
    val microwave: Boolean,
    val internet: Boolean,
    val appliancesCount: Int,
    val appliancesLevel: String
)

data class TenantRules(
    val petsAllowed: Boolean,
    val petsForbidden: Boolean,
    val childrenAllowed: Boolean,
    val childrenForbidden: Boolean,
    val smokingForbidden: Boolean,
    val longTermOnly: Boolean,
    val depositMentioned: Boolean,
    val utilitiesMentioned: Boolean
)

data class TextScores(
    val descriptionQualityScore: Int,
    val textFeatureScore: Double
)

data class PriceAnalysisResponse(
    val success: Boolean,
    val price: PriceResult
)

data class PriceResult(
    val recommendedPrice: Int,
    val minPrice: Int,
    val maxPrice: Int,
    val marketBasePrice: Int,
    val coefficients: PriceCoefficients,
    val priceFactors: List<String>
)

data class PriceCoefficients(
    val textCoefficient: Double,
    val imageCoefficient: Double,
    val geoCoefficient: Double
)

data class PriceImpact(
    val impact: String,
    val coefficient: Double,
    val description: String
)