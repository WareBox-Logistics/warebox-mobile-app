package com.example.lp_logistics.data.remote.api

import com.example.lp_logistics.data.remote.requests.CreateBoxRequest
import com.example.lp_logistics.data.remote.requests.CreatePalletRequest
import com.example.lp_logistics.data.remote.requests.DriverIdRequest
import com.example.lp_logistics.data.remote.requests.LoginRequest
import com.example.lp_logistics.data.remote.requests.ParkingLotRequest
import com.example.lp_logistics.data.remote.requests.ReportDispatchRequest
import com.example.lp_logistics.data.remote.responses.BoxResponseWithPallet
import com.example.lp_logistics.data.remote.responses.Companies
import com.example.lp_logistics.data.remote.responses.CompanyResponse
import com.example.lp_logistics.data.remote.responses.ConfirmationByQR
import com.example.lp_logistics.data.remote.responses.DeliveryData
import com.example.lp_logistics.data.remote.responses.DeliveryDock
import com.example.lp_logistics.data.remote.responses.DeliveryResponse
import com.example.lp_logistics.data.remote.responses.DeliveryStatusResponse
import com.example.lp_logistics.data.remote.responses.FreeLot
import com.example.lp_logistics.data.remote.responses.LoginResponse
import com.example.lp_logistics.data.remote.responses.PalletResponse
import com.example.lp_logistics.data.remote.responses.ParkingLotResponse
import com.example.lp_logistics.data.remote.responses.ProductResponse
import com.example.lp_logistics.data.remote.responses.ResponseMessage
import com.example.lp_logistics.data.remote.responses.RouteResponse
import com.example.lp_logistics.data.remote.responses.SimpleProductResponse
import com.example.lp_logistics.data.remote.responses.WarehouseResponse
import com.example.lp_logistics.data.remote.responses.Warehouses
import com.example.lp_logistics.data.repository.FcmTokenRequest
import com.example.lp_logistics.domain.model.DeliveryDetails
import com.example.lp_logistics.domain.model.Vehicle
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    //AUTH
    @POST("loginEmployee")
    suspend fun loginEmployee(
        @Body request: LoginRequest
    ): LoginResponse

    // Add this endpoint
    @PUT("users/{userId}/fcm-token")
    suspend fun updateFcmToken(
        @Path("userId") userId: Int,
        @Body request: FcmTokenRequest
    )

    @POST("logout")
    suspend fun logout(
        @Header("Authorization") token: String
    )

    //Routes testing
    @GET("route/{id}")
    suspend fun getRoute(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): RouteResponse

    //Create a Box
    @POST("box-inventory")
    suspend fun createBox(
        @Header("Authorization") token: String,
        @Body request: CreateBoxRequest
    )

    //Create a Pallet
    @POST("pallet")
    suspend fun createPallet(
        @Header("Authorization") token: String,
        @Body request: CreatePalletRequest
    ): PalletResponse

    //get warehouse
    @GET("warehouse/{id}")
    suspend fun getWarehouse(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): WarehouseResponse

    //get company
    @GET("company/{id}")
    suspend fun getCompany(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): CompanyResponse

    //get product
    @GET("product/{id}")
    suspend fun getProduct(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ):ProductResponse

    //get pallet
    @GET("pallet/{id}")
    suspend fun getPallet(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): PalletResponse

    //get box
    @GET("box-inventory/{id}")
    suspend fun getBox(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): BoxResponseWithPallet

    //get all companies
    @GET("company")
    suspend fun getAllCompanies(
        @Header("Authorization") token: String
    ): Companies

    //get all warehouses
    @GET("warehouse")
    suspend fun getAllWarehouses(
        @Header("Authorization") token: String
    ): Warehouses

    @GET("product/company/{company}")
    suspend fun getProductsByCompany(
        @Header("Authorization") token: String,
        @Path("company") company: Int
    ): List<SimpleProductResponse>

    @POST("delivery-driver")
    suspend fun getDeliveryOrders(
        @Header("Authorization") token: String,
        @Body request: DriverIdRequest
    ): DeliveryResponse

    @POST("lots/vehicle/location")
    suspend fun getParkingLot(
        @Header("Authorization") token: String,
        @Body request: ParkingLotRequest
    ): ParkingLotResponse

    @POST("lots/vehicle/location")
    suspend fun getParkingLotTrailer(
        @Header("Authorization") token: String,
        @Body request: ParkingLotRequest
    ): ParkingLotResponse

    @GET("delivery/{id}")
    suspend fun getDelivery(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): DeliveryData


    @GET("delivery/filtered/{id}")
    suspend fun getDeliveryDetails(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): DeliveryDetails

    @POST("report")
    suspend fun postReportForDispatch(
        @Header("Authorization") token: String,
        @Body request: ReportDispatchRequest
    )

    @GET("deliveries/{deliveryId}/dock-assignment")
    suspend fun getReservedDock(
        @Header("Authorization") token: String,
        @Path("deliveryId") deliveryId: Int
    ): DeliveryDock

    @PATCH("docks/{id}/set-loading")
    suspend fun setLoadingDock(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
    ): ResponseMessage

    @PATCH("deliveries/{deliveryId}/start-delivering")
    suspend fun startDelivering(
        @Header("Authorization") token: String,
        @Path("deliveryId") deliveryId: Int
    ): ResponseMessage

    @PATCH("deliveries/{deliveryId}/set-docking")
    suspend fun setToDocking(
        @Header("Authorization") token: String,
        @Path("deliveryId") deliveryId: Int
    ): ResponseMessage

    @GET("deliveries/{deliveryId}/status")
    suspend fun getDeliveryStatus(
        @Header("Authorization") token: String,
        @Path("deliveryId") deliveryId: Int
    ): DeliveryStatusResponse

    @PATCH("deliveries/{deliveryId}/complete")
    suspend fun confirmDeliveryArrival(
        @Header("Authorization") token: String,
        @Path("deliveryId") deliveryId: Int
    ): ResponseMessage

    @GET("driver/{driverId}/vehicle")
    suspend fun getDriverVehicle(
        @Header("Authorization") token: String,
        @Path("driverId") driverId: Int
    ): Vehicle

    @POST("deliveries/confirm-by-code")
    suspend fun confirmDeliveryByCode(
        @Header("Authorization") token: String,
        @Body request: ConfirmationByQR
    ): ResponseMessage

    @POST("lots/free")
    suspend fun freeLot(
        @Header("Authorization") token: String,
        @Body request: FreeLot
    )
}

