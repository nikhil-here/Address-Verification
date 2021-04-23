package com.nikhilhere.test.consumer.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.nikhilhere.test.consumer.databinding.ComponentListItemRecentSearchBinding
import com.nikhilhere.test.consumer.util.MyDiffUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AdapterRecentSearch : RecyclerView.Adapter<AdapterRecentSearch.MyViewHolder> (){

    private var placesList = emptyList<AutocompletePrediction>()
    private val _placeId = MutableStateFlow("")
    val placeId : StateFlow<String> get() = _placeId
    private var TAG = "AdapterRecentSearch"

   inner class MyViewHolder (private val binding : ComponentListItemRecentSearchBinding) : RecyclerView.ViewHolder(binding.root){

        init {
            binding.apply {
                root.setOnClickListener{
                    setPlaceId(placesList[adapterPosition].placeId)
                }
            }
        }
        fun bind(prediction : AutocompletePrediction){
            binding.tvPrimaryText.text = prediction.getPrimaryText(null).toString()
            binding.tvSecondaryText.text = prediction.getSecondaryText(null).toString()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ComponentListItemRecentSearchBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentPlace = placesList[position]
        holder.bind(currentPlace)
        Log.i(TAG, "onBindViewHolder Position ${position} Current Place ${currentPlace.toString()}")
    }

    override fun getItemCount(): Int {
        return placesList.size
    }

    private fun setPlaceId(placeId : String){
        _placeId.value = placeId
    }

    fun setData(newPlacesList : List<AutocompletePrediction>){
        val myDiffUtil = MyDiffUtil(placesList,newPlacesList)
        val myDiffUtilResult = DiffUtil.calculateDiff(myDiffUtil)
        placesList = newPlacesList
        myDiffUtilResult.dispatchUpdatesTo(this)
    }


}


