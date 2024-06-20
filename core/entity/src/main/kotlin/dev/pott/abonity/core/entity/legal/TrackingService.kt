package dev.pott.abonity.core.entity.legal

enum class TrackingService(val serviceId: ConsentServiceId, val serviceCategory: ServiceCategory) {
    FIREBASE_ANALYTICS(
        serviceId = ConsentServiceId.FIREBASE_ANALYTICS,
        serviceCategory = ServiceCategory.ANALYTICS,
    ),
    FIREBASE_CRASHLYTICS(
        serviceId = ConsentServiceId.FIREBASE_CRASHLYTICS,
        serviceCategory = ServiceCategory.MONITORING,
    ),
    FIREBASE_PERFORMANCE(
        serviceId = ConsentServiceId.FIREBASE_PERFORMANCE,
        serviceCategory = ServiceCategory.MONITORING,
    ),
    FIREBASE_CLOUD_MESSAGING(
        serviceId = ConsentServiceId.FIREBASE_CLOUD_MESSAGING,
        serviceCategory = ServiceCategory.REQUIRED,
    ),
    FIREBASE_IN_APP_MESSAGING(
        serviceId = ConsentServiceId.FIREBASE_IN_APP_MESSAGING,
        serviceCategory = ServiceCategory.REQUIRED,
    ),
    FIREBASE_INSTALLATIONS(
        serviceId = ConsentServiceId.FIREBASE_INSTALLATIONS,
        serviceCategory = ServiceCategory.REQUIRED,
    ),
    FIREBASE_REMOTE_CONFIG(
        serviceId = ConsentServiceId.FIREBASE_REMOTE_CONFIG,
        serviceCategory = ServiceCategory.REQUIRED,
    ),
}
