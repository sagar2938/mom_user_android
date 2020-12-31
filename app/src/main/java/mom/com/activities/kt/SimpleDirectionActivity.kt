package mom.com.activities.kt

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.View

import com.akexorcist.googledirection.GoogleDirection
import com.akexorcist.googledirection.config.GoogleDirectionConfiguration
import com.akexorcist.googledirection.constant.TransportMode
import com.akexorcist.googledirection.model.Direction
import com.akexorcist.googledirection.model.Route
import com.akexorcist.googledirection.util.DirectionConverter
import com.akexorcist.googledirection.util.execute
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_simple_direction.*
import mom.com.BuildConfig
import mom.com.R
import mom.com.WebService.ResponseCallBack
import mom.com.WebService.WebServiceHelper
import mom.com.helper.Demo
import mom.com.network.ApiCallService
import mom.com.network.ThisApp
import mom.com.utils.Preferences
import org.json.JSONObject
import java.util.*
import kotlin.collections.HashMap

class SimpleDirectionActivity : AppCompatActivity() {


    internal var latitude: Double? = null
    internal var longitude: Double? = null
    internal lateinit var orderId: String

    companion object {
        private const val serverKey = "AIzaSyDFrE1WgFtmWfWPExKEnreTaFdsyqJLVfs"
        private var origin = LatLng(37.7849569, -122.4068855)
        private var destination = LatLng(37.7814432, -122.4460177)
    }

    private var googleMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple_direction)


        buttonRequestDirection.setOnClickListener { requestDirection() }

        (supportFragmentManager.findFragmentById(R.id.maps) as SupportMapFragment).getMapAsync { googleMap ->
            this.googleMap = googleMap
        }

        api()


    }

    private fun requestDirection() {
        showSnackbar(getString(R.string.direction_requesting))
        GoogleDirectionConfiguration.getInstance().isLogEnabled = BuildConfig.DEBUG
        GoogleDirection.withServerKey(serverKey)
                .from(origin)
                .to(destination)
                .transportMode(TransportMode.DRIVING)
                .execute(
                        onDirectionSuccess = { direction -> onDirectionSuccess(direction) },
                        onDirectionFailure = { t -> onDirectionFailure(t) }
                )
    }



    private fun api() {


        latitude = java.lang.Double.valueOf(intent.getStringExtra("latitude"))
        longitude = java.lang.Double.valueOf(intent.getStringExtra("longitude"))
        orderId = intent.getStringExtra("orderId")

        val rootObject= JSONObject()
        rootObject.put("mobile",Preferences.getInstance(applicationContext).mobile)
        rootObject.put("latitude",java.lang.Double.valueOf(intent.getStringExtra("latitude")))
        rootObject.put("longitude",java.lang.Double.valueOf(intent.getStringExtra("longitude")))
        rootObject.put("orderId",orderId)
        WebServiceHelper.getInstance().PostCall(this, "https://mom-apicalls.appspot.com/api/delivery/location/", rootObject, object : ResponseCallBack {
            override fun OnResponse(Response: JSONObject) {

                var v=Response.getJSONObject("response").getJSONArray("deliver_data").getJSONObject(0)
                origin = LatLng(java.lang.Double.valueOf(intent.getStringExtra("latitude")), java.lang.Double.valueOf(intent.getStringExtra("longitude")))
                destination = LatLng(v.getDouble("deliver_lat"), v.getDouble("deliver_long"))
                var statusInt=Response.getJSONObject("response").getJSONArray("deliver_data").getJSONObject(0).getInt("orderStatus")
                if (statusInt==0){
                    status.setText("Waiting for confirmation from MOM Chef")
                }else if (statusInt==1){
                    status.setText("Accepted! Your food is being prepared")
                }else if (statusInt==2){
                    status.setText("Your order has been assigned")
                }else if (statusInt==3){
                    status.setText("Your order is on the way")
                }
                requestDirection()

//                Response.getJSONObject("response").getJSONArray("deliver_data").getJSONObject(0).getDouble("deliver_lat")
//                Response.getJSONObject("response").getJSONArray("deliver_data").getJSONObject(0).getDouble("deliver_long")
//                println("http " + "https://mom-apicalls.appspot.com/api/delivery/location/")
//                println("http request " + rootObject)
//                println("http response " + Response)
                showSnackbar(getString(R.string.success))
                Handler().postDelayed({
                    api()
                }, 5000)
            }

            override fun OnError(Response: JSONObject) {

            }
        })




    }

    private fun onDirectionSuccess(direction: Direction) {
        showSnackbar(getString(R.string.success_with_status, direction.status))
        if (direction.isOK) {
            val route = direction.routeList[0]
            googleMap?.addMarker(MarkerOptions().position(origin))
            googleMap?.addMarker(MarkerOptions().position(destination))
            val directionPositionList = route.legList[0].directionPoint
            googleMap?.addPolyline(DirectionConverter.createPolyline(this, directionPositionList, 5, Color.RED))
            setCameraWithCoordinationBounds(route)
            buttonRequestDirection.visibility = View.GONE
        } else {
            showSnackbar(direction.status)
        }
    }

    private fun onDirectionFailure(t: Throwable) {
        showSnackbar(t.message)
    }

    private fun setCameraWithCoordinationBounds(route: Route) {
        val southwest = route.bound.southwestCoordination.coordination
        val northeast = route.bound.northeastCoordination.coordination
        val bounds = LatLngBounds(southwest, northeast)
        googleMap?.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
    }

    private fun showSnackbar(message: String?) {
        message?.let {
            Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show()
        }
    }
}
