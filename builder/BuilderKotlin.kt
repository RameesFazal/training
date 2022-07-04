package com.example.kotlintraining.designpattern.builder

enum class CarType {
    CITY_CAR, SPORTS_CAR, SUV
}

enum class Transmission {
    SINGLE_SPEED, MANUAL, AUTOMATIC, SEMI_AUTOMATIC
}

class Car(
    val carType: CarType, val seats: Int, val engine: Engine, val transmission: Transmission,
    val tripComputer: TripComputer, gpsNavigator: GPSNavigator
) {
    val gpsNavigator: GPSNavigator
    var fuel = 0.0

    init {
        tripComputer.setCar(this)
        this.gpsNavigator = gpsNavigator
    }
}

class TripComputer {

    private var car: Car? = null

    fun setCar(car: Car) {
        this.car = car
    }

    fun showFuelLevel() {
        println("Fuel level: " + car?.fuel)
    }

    fun showStatus() {
        if (car?.engine?.isStarted == true) {
            println("Car is started")
        } else {
            println("Car is not started")
        }
    }
}

class Engine(val volume: Double, var mileage: Double) {
    var isStarted = false
        private set

    fun on() {
        isStarted = true
    }

    fun off() {
        isStarted = false
    }

    fun go(mileage: Double) {
        if (isStarted) {
            this.mileage += mileage
        } else {
            System.err.println("Cannot go, you must start engine first!")
        }
    }

}

class GPSNavigator {
    var route: String
        private set

    constructor() {
        route = "1c 101 bhandup west mumbai"
    }

    constructor(manualRoute: String) {
        route = manualRoute
    }
}

interface Builder {
    fun setCarType(type: CarType?)
    fun setSeats(seats: Int)
    fun setEngine(engine: Engine)
    fun setTransmission(transmission: Transmission)
    fun setTripComputer(tripComputer: TripComputer)
    fun setGPSNavigator(gpsNavigator: GPSNavigator)
}

class Director {
    fun constructSportsCar(builder: Builder) {
        builder.setCarType(CarType.SPORTS_CAR)
        builder.setSeats(2)
        builder.setEngine(Engine(3.0, 0.0))
        builder.setTransmission(Transmission.SEMI_AUTOMATIC)
        builder.setTripComputer(TripComputer())
        builder.setGPSNavigator(GPSNavigator())
    }

    fun constructCityCar(builder: Builder) {
        builder.setCarType(CarType.CITY_CAR)
        builder.setSeats(2)
        builder.setEngine(Engine(1.2, 0.0))
        builder.setTransmission(Transmission.AUTOMATIC)
        builder.setTripComputer(TripComputer())
        builder.setGPSNavigator(GPSNavigator())
    }

    fun constructSUV(builder: Builder) {
        builder.setCarType(CarType.SUV)
        builder.setSeats(4)
        builder.setEngine(Engine(2.5, 0.0))
        builder.setTransmission(Transmission.MANUAL)
        builder.setGPSNavigator(GPSNavigator())
    }
}

class CarBuilder : Builder {
    private var type: CarType? = null
    private var seats = 0
    private var engine: Engine? = null
    private var transmission: Transmission? = null
    private var tripComputer: TripComputer? = null
    private var gpsNavigator: GPSNavigator? = null
    override fun setCarType(type: CarType?) {
        this.type = type
    }

    override fun setSeats(seats: Int) {
        this.seats = seats
    }

    override fun setEngine(engine: Engine) {
        this.engine = engine
    }

    override fun setTransmission(transmission: Transmission) {
        this.transmission = transmission
    }

    override fun setTripComputer(tripComputer: TripComputer) {
        this.tripComputer = tripComputer
    }

    override fun setGPSNavigator(gpsNavigator: GPSNavigator) {
        this.gpsNavigator = gpsNavigator
    }

    val result: Car
        get() = Car(
            type!!, seats,
            engine!!, transmission!!, tripComputer!!, gpsNavigator!!
        )
}

fun main() {
    val director = Director()
    val builder = CarBuilder()
    director.constructCityCar(builder)
    val car = builder.result
    println("Car built: ${car.carType}")
}