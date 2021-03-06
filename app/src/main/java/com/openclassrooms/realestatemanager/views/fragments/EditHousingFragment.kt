package com.openclassrooms.realestatemanager.views.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.models.CompleteHousing
import com.openclassrooms.realestatemanager.models.Photo
import com.openclassrooms.realestatemanager.utils.STRING_EMPTY
import com.openclassrooms.realestatemanager.utils.Utils
import com.openclassrooms.realestatemanager.utils.UtilsKotlin
import kotlinx.android.synthetic.main.fragment_add_housing.view.*
import kotlinx.android.synthetic.main.fragment_add_housing.view.add_housing_fragment_zipCode_editTxt
import kotlinx.android.synthetic.main.progress_bar.view.*

/**
 * A simple [Fragment] subclass.
 */
class EditHousingFragment : BaseEditHousingFragment() {

    private lateinit var housingToCompare : CompleteHousing
    private  var mAdapterType : ArrayAdapter<CharSequence>? = null
    private  var mAdapterState : ArrayAdapter<CharSequence>? = null
    private  var mAdapterRooms : ArrayAdapter<CharSequence>? = null
    private  var mAdapterBedRooms : ArrayAdapter<CharSequence>? = null
    private  var mAdapterBathRooms : ArrayAdapter<CharSequence>? = null
    private  var mAdapterCountry : ArrayAdapter<CharSequence>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        this.mView.progress_bar_layout.visibility = View.VISIBLE

        this.configureSpinnersEdit()
        this.isLoadingEdit = true
        this.getHousing()

        this.mView.add_housing_fragment_final_button.setImageResource(R.drawable.ic_baseline_save_24)
        this.mView.add_housing_fragment_final_button.setOnClickListener {
            this.updateFinal()
        }
        return mView
    }

    /**
     * Link with the ViewModel to update a CompleteHousing in RoomDatabase
     */
    private fun updateFinal()
    {
        this.closeKeyboard(requireContext(), mView)
        this.checkAddress()
        context?.let {
            housing.lastUpdate = Utils.getTodayDateGood()
            this.mViewModel.updateGlobalHousing(housingToCompare, housing, address, photoList, estateAgentList, it, mApiKey, isInternetAvailable, this.findNavController())
        }
    }

    /**
     * Get the housing to Edit
     */
    private fun getHousing()
    {
        this.mViewModel.getCompleteHousing(housingReference).observe(this.viewLifecycleOwner, Observer {
            housingToCompare = it
            configureData()
            this.mView.progress_bar_layout.visibility = View.GONE
        })
    }

    private fun configureData()
    {
        this.housing = this.housingToCompare.housing.copy()

        housingToCompare.housing.price.let { this.mView.add_housing_fragment_price_editTxt.setText(it.toString()) }
        housingToCompare.housing.area.let { this.mView.add_housing_fragment_area_editTxt.setText(it.toString()) }
        housingToCompare.housing.area.let { this.mView.add_housing_fragment_area_editTxt.setText(it.toString()) }
        housingToCompare.housing.description?.let { this.mView.add_housing_fragment_description_editTxt.setText(it) }

        housingToCompare.housing.type.let { this.mView.add_housing_fragment_type_spinner.setSelection(mAdapterType!!.getPosition(it))}
        housingToCompare.housing.state.let { this.mView.add_housing_fragment_state_spinner.setSelection(mAdapterState!!.getPosition(it)) }
        housingToCompare.housing.rooms?.let { this.mView.add_housing_fragment_number_rooms_spinner.setSelection(mAdapterRooms!!.getPosition(it.toString())) }
        housingToCompare.housing.bedrooms?.let { this.mView.add_housing_fragment_number_bedrooms_spinner.setSelection(mAdapterBedRooms!!.getPosition(it.toString())) }
        housingToCompare.housing.bathrooms?.let { this.mView.add_housing_fragment_number_bathrooms_spinner.setSelection(mAdapterBathRooms!!.getPosition(it.toString())) }

        if (housingToCompare.address != null)
        {
            this.address = this.housingToCompare.address!!.copy()
            housingToCompare.address!!.street.let { this.mView.add_housing_fragment_address_editTxt.setText(it) }
            housingToCompare.address!!.zipCode?.let { this.mView.add_housing_fragment_zipCode_editTxt.setText(it.toString()) }
            housingToCompare.address!!.city.let { this.mView.add_housing_fragment_city_editTxt.setText(it) }
            housingToCompare.address!!.country.let { this.mView.add_housing_fragment_country_spinner.setSelection(mAdapterCountry!!.getPosition(it)) }
        }

        housingToCompare.photoList?.let {
            for (i in it)
            {
                this.photoList.add(i)
            }
            mAdapterPhotoAddRcv.updateList(this.photoList)

        }
        housingToCompare.estateAgentList?.let {
            for (i in it)
            {
                this.estateAgentList.add(i)
            }
            mAdapterEstateAgentRcv.updateList(this.estateAgentList)
        }
    }

    private fun configureSpinnersEdit()
    {
        configureSpinnerAdapter(R.array.type_housing_spinner)?.let { this.mAdapterType = it }
        this.mView.add_housing_fragment_type_spinner.adapter = mAdapterType
        configureSpinnerAdapter(R.array.state_spinner)?.let { this.mAdapterState = it }
        this.mView.add_housing_fragment_state_spinner.adapter = mAdapterState
        configureSpinnerAdapter(R.array.number_rooms)?.let {
            this.mAdapterRooms = it
            this.mAdapterBedRooms = it
            this.mAdapterBathRooms = it
        }
        this.mView.add_housing_fragment_number_rooms_spinner.adapter = mAdapterRooms
        this.mView.add_housing_fragment_number_bedrooms_spinner.adapter = mAdapterBedRooms
        this.mView.add_housing_fragment_number_bathrooms_spinner.adapter = mAdapterBathRooms
        configureSpinnerAdapter(R.array.country_spinner)?.let { this.mAdapterCountry = it }
        this.mView.add_housing_fragment_country_spinner.adapter = mAdapterCountry
    }

    override fun onClickDeleteEstateAgent(position: Int) {

        val builder = AlertDialog.Builder(context)
        builder.setMessage(resources.getString(R.string.sure_delete))
                .setPositiveButton(getString(R.string.yes), DialogInterface.OnClickListener { _, _ ->
                    if (estateAgentList.size <= 1)
                    {
                        estateAgentList.clear()
                    }
                    else
                    {
                        val estateAgentToDelete = this.estateAgentList[position]
                        this.estateAgentList.remove(estateAgentToDelete)
                    }
                    this.mAdapterEstateAgentRcv.updateList(estateAgentList)
                })
                .setNegativeButton(getString(R.string.no), DialogInterface.OnClickListener { dialog, _ -> dialog.cancel() })

        val alertDialog = builder.create()
        alertDialog.show()


    }

    override fun onClickEditPhoto(position: Int) {
        val photoToEdit = this.photoList[position]

        UtilsKotlin.displayPhoto(isInternetAvailable, photoToEdit, mView, mView.add_housing_fragment_photo_image, requireContext())

        photoToEdit.description?.let { this.mView.add_housing_fragment_image_description_editTxt.setText(it) }

        this.mView.add_housing_fragment_photo_image.isEnabled = false

        var description = STRING_EMPTY
        this.mView.add_housing_fragment_image_description_editTxt.doAfterTextChanged { description = it.toString() }

        this.mView.add_housing_fragment_photo_button.setOnClickListener {
            val photo = Photo(photoToEdit.uri, description, housingReference, photoToEdit.url_firebase)
            photoList[position] = photo
            mAdapterPhotoAddRcv.updateList(photoList)

            //Clear photo and description
            this.mView.add_housing_fragment_photo_image.isEnabled = true
            this.mView.add_housing_fragment_photo_image.setImageResource(R.drawable.ic_baseline_add_photo_camera_48)
            description = STRING_EMPTY
            this.mView.add_housing_fragment_image_description_editTxt.text.clear()

        }
    }

    override fun onClickDeletePhoto(position: Int) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage(resources.getString(R.string.sure_delete))
                .setPositiveButton(getString(R.string.yes), DialogInterface.OnClickListener { _, _ ->
                    if (photoList.size <= 1)
                    {
                        photoList.clear()
                    }
                    else
                    {
                        val photoToDelete = this.photoList[position]
                        this.photoList.remove(photoToDelete)
                    }
                    this.mAdapterPhotoAddRcv.updateList(photoList)
                })
                .setNegativeButton(getString(R.string.no), DialogInterface.OnClickListener { dialog, _ -> dialog.cancel() })

        val alertDialog = builder.create()
        alertDialog.show()
    }
}