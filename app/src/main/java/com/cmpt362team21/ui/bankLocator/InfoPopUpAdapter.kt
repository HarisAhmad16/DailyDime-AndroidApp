package com.cmpt362team21.ui.bankLocator

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.cmpt362team21.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

//https://www.youtube.com/watch?v=DhYofrJPzlI&list=PLgCYzUzKIBE-vInwQhGSdnbyJ62nixHCt&index=11
//Referred to this series for help with this feature
class InfoPopUpAdapter(private val context: Context) : GoogleMap.InfoWindowAdapter {

    private val window: View by lazy {
        LayoutInflater.from(context).inflate(R.layout.info_marker_pop_up, null)
    }

    private fun getInfoContents(marker: Marker, view: View) {
        val infoTitle = marker.title
        val infoTitleTextView = view.findViewById<TextView>(R.id.infoTitleText)

        if (!infoTitle.isNullOrEmpty()) {
            infoTitleTextView.text = infoTitle
        }

        val infoBody = marker.snippet
        val infoBodyTextView = view.findViewById<TextView>(R.id.infoBodyText)

        if (!infoBody.isNullOrEmpty()) {
            infoBodyTextView.text = infoBody
        }
    }

    override fun getInfoContents(marker: Marker): View? {
        getInfoContents(marker, window)
        return window
    }

    override fun getInfoWindow(marker: Marker): View? {
        getInfoContents(marker, window)
        return window
    }
}
