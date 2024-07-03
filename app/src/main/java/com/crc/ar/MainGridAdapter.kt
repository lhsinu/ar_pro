package com.crc.ar

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView

class MainGridAdapter (val context: Context, val mainGridList: ArrayList<Int>): BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = LayoutInflater.from(context).inflate(R.layout.main_gv_item, null)

        val ivGridIcon = view.findViewById<ImageView>(R.id.iv_grid_icon)

        val nMainIcon = mainGridList[position]

        ivGridIcon.setImageResource(nMainIcon)

        return view
    }

    override fun getItem(position: Int): Any {
        return mainGridList[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return mainGridList.size
    }
}