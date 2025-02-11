package com.example.places.carappservice

import androidx.car.app.CarAppService
import androidx.car.app.Session
import androidx.car.app.validation.HostValidator

class CarAppService : CarAppService() {

    /**
    ensure that the host is trusted, it fails if the host doesnt mathch the parameters
    ALLOW_ALL_HOSTS_VALIDATOR should be used only in dev environment not in production
     */
    override fun createHostValidator(): HostValidator {
        return HostValidator.ALLOW_ALL_HOSTS_VALIDATOR
    }

    /**
    It is the entry for host application to communicate with client apps.
    Main purpose is create Session instances that the host app interacts with
     */
    override fun onCreateSession(): Session {
        // CarAppSession will be an unresolved reference until the next step
        return CarAppSession()
    }
}
