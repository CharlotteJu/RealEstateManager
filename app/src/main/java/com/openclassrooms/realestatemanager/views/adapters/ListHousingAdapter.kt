package com.openclassrooms.realestatemanager.views.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.models.CompleteHousing
import kotlinx.android.synthetic.main.item_housing.view.*
import kotlinx.android.synthetic.main.item_photo.view.*

class ListHousingAdapter(private var listHousing : List<CompleteHousing>)  : RecyclerView.Adapter<ListHousingAdapter.ListHousingViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListHousingViewHolder
    {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_housing, parent, false)
        return ListHousingViewHolder(view)
    }


    override fun onBindViewHolder(holder: ListHousingViewHolder, position: Int)
    {
        val housing = this.listHousing[position]
        holder.configureDesign(housing)
    }

    override fun getItemCount(): Int = this.listHousing.size

    fun updateList(listHousing: List<CompleteHousing>)
    {
        this.listHousing = listHousing
        this.notifyDataSetChanged()
    }

    class ListHousingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        fun configureDesign(housing: CompleteHousing)
        {
            this.configPhoto(housing)
            this.configText(housing)
        }

        private fun configPhoto(housing : CompleteHousing)
        {
           /*if (housing.photoList != null)
           {
               housing.photoList!![0].uri.let {
                   Glide.with(itemView)
                           .load(it)
                           .apply(RequestOptions.centerCropTransform())
                           .into(itemView.item_photo_image)
               }
           }
            else
           {
               itemView.item_photo_image.visibility = View.INVISIBLE
           }*/

        }

        private fun configText(housing : CompleteHousing)
        {
            housing.housing.type.let {
                itemView.item_housing_type_txt.text = it
            }

            if (housing.address != null)
            {
                housing.address!!.city.let {
                    itemView.item_housing_district_txt.text = it
                }

            }
            else
            {
                itemView.item_housing_district_txt.visibility = View.GONE
            }

            housing.housing.price.let {
                val priceString = "$it$"
                itemView.item_housing_price_txt.text = priceString //TODO : Voir conversion && pas de puissance
            }
        }


    }
}
