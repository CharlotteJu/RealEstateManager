package com.openclassrooms.realestatemanager.views.fragments

import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.models.CompleteHousing
import com.openclassrooms.realestatemanager.utils.*
import com.openclassrooms.realestatemanager.viewModels.DetailViewModel
import com.openclassrooms.realestatemanager.views.activities.MainActivity
import kotlinx.android.synthetic.main.fragment_map_complete.view.*


import org.koin.androidx.viewmodel.ext.android.viewModel

class MapFragment : BaseFragment(), OnMapReadyCallback {

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var mMap: GoogleMap
    private var mCurrentLocation: Location? = null
    private val mViewModel : DetailViewModel by viewModel()
    private var mListHousing : MutableList<CompleteHousing> = arrayListOf()
    private lateinit var currency : String

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        this.mFusedLocationClient = requireActivity().let { LocationServices.getFusedLocationProviderClient(it) }
        this.currency = getCurrencyFromSharedPreferences()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) UtilsPermissions.checkLocationPermission(requireActivity())

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    {
        val view: View = inflater.inflate(R.layout.fragment_map_complete, container, false)
        val mapFragment = FragmentManager.findFragment<SupportMapFragment>(view.fragment_map_complete_map)
        this.fetchLocation()
        this.mViewModel.getAllCompleteHousing().observe(this.viewLifecycleOwner, Observer {
            this.mListHousing = it as MutableList<CompleteHousing>
            this.createMarkers()
        })
        mapFragment.getMapAsync(this)

        view.fragment_map_complete_fab.setOnClickListener {
            findNavController().navigate(R.id.listFragment)
        }

        return view
    }

    override fun onMapReady(p0: GoogleMap?) {
        if (p0 != null) mMap = p0
        if (mCurrentLocation != null) {
            mMap.apply {
                val marker = LatLng(mCurrentLocation!!.latitude, mCurrentLocation!!.longitude)
                addMarker(MarkerOptions().position(marker))
                moveCamera(CameraUpdateFactory.newLatLngZoom(marker, 15f))
            }
        }
    }

    private fun createMarkers()
    {
        for (housing in mListHousing)
        {
            if (housing.address != null)
            {
                val geocoder = Geocoder(context)
                val listGeocoder  = geocoder.getFromLocationName(housing.address.toString(), 1)
                val lat  = listGeocoder[0].latitude
                val lng = listGeocoder[0].longitude
                val latLng = LatLng(lat, lng)
                val price : String?

                if (currency == DOLLAR)
                {
                    housing.housing.price.let { price = "$it$" }
                }
                else
                {
                    housing.housing.price.let {
                        val euroPrice = Utils.convertDollarToEuroDouble(it)
                        price = "$euroPrice€"
                    }
                }

                val tempMarker : MarkerOptions = MarkerOptions().position(latLng).title("${housing.housing.type} - $price")
                val finalMarker = mMap.addMarker(tempMarker)
                finalMarker.tag = housing.housing.ref

                this.mMap.setOnMarkerClickListener { p0 ->
                    if (p0 != null) {
                        p0.showInfoWindow()
                        true
                    } else false
                }

                this.mMap.setOnInfoWindowClickListener {

                    if (it.tag != null)
                    {
                        if (this.getIsTabletFromSharedPreferences() && (activity as MainActivity).isLandMode())
                        {
                            val detailFragment = (activity as MainActivity).getDetailFragment()
                            detailFragment?.updateRef(it.tag.toString(), requireContext())
                        }
                        else
                        {
                            val bundle = Bundle ()
                            bundle.putString(BUNDLE_REFERENCE, it.tag.toString())
                            findNavController().navigate(R.id.detailFragment, bundle)
                        }
                    }
                }

            }
        }
    }


    private fun fetchLocation()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) UtilsPermissions.checkLocationPermission(requireActivity())

        val task = mFusedLocationClient.lastLocation
        task.addOnSuccessListener {
            if (it != null) {
                mCurrentLocation = it
                mMap.apply {
                    val marker = LatLng(mCurrentLocation!!.latitude, mCurrentLocation!!.longitude)
                    addMarker(MarkerOptions().position(marker).title(getString(R.string.my_position)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)))
                    moveCamera(CameraUpdateFactory.newLatLngZoom(marker, 15f))
                }
            } else {
                val locationManager: LocationManager = requireActivity().getSystemService(LOCATION_SERVICE) as LocationManager

                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                    this.fetchLocation()
                } else {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, object : LocationListener {
                        override fun onLocationChanged(location: Location?) {
                            mCurrentLocation = location
                            mMap.apply {
                                val marker = LatLng(mCurrentLocation!!.latitude, mCurrentLocation!!.longitude)
                                addMarker(MarkerOptions().position(marker))
                                moveCamera(CameraUpdateFactory.newLatLngZoom(marker, 15f))
                            }
                        }

                        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

                        override fun onProviderEnabled(provider: String?) {}

                        override fun onProviderDisabled(provider: String?) {}
                    })
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_CODE)
        {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) fetchLocation()
        }
    }
}