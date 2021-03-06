package com.openclassrooms.realestatemanager.views.adapters


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.models.CompleteHousing
import com.openclassrooms.realestatemanager.utils.DOLLAR
import com.openclassrooms.realestatemanager.utils.Utils
import com.openclassrooms.realestatemanager.utils.UtilsKotlin
import kotlinx.android.synthetic.main.item_housing.view.*

class ListHousingAdapter(private var listHousing : List<CompleteHousing>, private val onItemClickListener: OnItemClickListener, private val currency : String, private val isInternetAvailable : Boolean, private val context : Context)  : RecyclerView.Adapter<ListHousingAdapter.ListHousingViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListHousingViewHolder
    {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_housing, parent, false)
        return ListHousingViewHolder(view, this.onItemClickListener, this.currency, this.isInternetAvailable, context)
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

    class ListHousingViewHolder(itemView: View, private val onItemClickListener: OnItemClickListener, private val currency: String, private val isInternetAvailable : Boolean, private val context: Context) : RecyclerView.ViewHolder(itemView)
    {
        fun configureDesign(housing: CompleteHousing)
        {
            this.configPhoto(housing)
            this.configText(housing)

            itemView.tag = housing.housing.ref

            itemView.setOnClickListener{ onItemClickListener.onItemClick(adapterPosition) }
        }

        private fun configPhoto(housing : CompleteHousing)
        {
           if (housing.photoList != null && housing.photoList!!.isNotEmpty())
           {
               UtilsKotlin.displayPhoto(isInternetAvailable, housing.photoList!![0], itemView, itemView.item_housing_image, context)
           }
            else
           {
               itemView.item_housing_image.setImageResource(R.drawable.ic_baseline_no_picture)
           }

        }

        private fun configText(housing : CompleteHousing)
        {
            housing.housing.type.let { itemView.item_housing_type_txt.text = it }

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

            //Update price according to the currency
            val priceString : String?
            if (currency == DOLLAR)
            {
                housing.housing.price.let {
                    priceString = "$it$"
                }
            }
            else
            {
                housing.housing.price.let {
                    val euroPrice = Utils.convertDollarToEuroDouble(it)
                    priceString = "$euroPrice€"
                }
            }
            itemView.item_housing_price_txt.text = priceString
        }

    }
}

