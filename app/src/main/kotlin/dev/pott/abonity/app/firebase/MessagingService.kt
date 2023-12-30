package dev.pott.abonity.app.firebase

import co.touchlab.kermit.Logger
import com.google.firebase.messaging.FirebaseMessagingService

class MessagingService : FirebaseMessagingService() {

    private val logger = Logger.withTag(this::class.simpleName.orEmpty())
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        logger.v { "New Firebase Messaging Token received: '$token'" }
    }
}
