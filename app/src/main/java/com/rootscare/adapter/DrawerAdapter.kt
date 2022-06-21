package com.rootscare.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.bumptech.glide.Glide
import com.rootscare.R
import com.interfaces.OnItemClickListener
import com.model.DrawerDataType

import java.util.LinkedList

class DrawerAdapter(private val context: Context, drawerDatatypes: LinkedList<DrawerDataType>) :
    RecyclerView.Adapter<DrawerAdapter.ViewHolder>() {

    internal lateinit var recyclerView: RecyclerView
    var expandedPosition = -1
    private var onItemClickListener: OnItemClickListener? = null
    private var drawerDatatypes = LinkedList<DrawerDataType>()

    init {
        this.drawerDatatypes = drawerDatatypes
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.navigation_drawer_single_item, null)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        val layoutManager = recyclerView.layoutManager
        holder.item_tv.width = layoutManager!!.width

        holder.item_parent_ll.setOnClickListener {

            expandedPosition = position
            notifyDataSetChanged()
            onItemClickListener!!.onItemClick(position, holder.item_tv)
//            if (expandedPosition >= 0) {
//                val prev = expandedPosition
////                notifyItemChanged(prev)
//               // notifyDataSetChanged()
//            }
//
//            if (position == expandedPosition) {
//                /*expandedPosition = -1;
//                    notifyDataSetChanged();*/
//
//                // For Multitple click on last item
//                if (position == drawerDatatypes.size - 1) {
//                    if (onItemClickListener != null) {
//                        notifyDataSetChanged()
//                        onItemClickListener!!.onItemClick(position, holder.item_tv)
//                    }
//                }
//            } else {
//                expandedPosition = position
//
//                if (onItemClickListener != null){
//                    notifyDataSetChanged()
//                    onItemClickListener!!.onItemClick(position, holder.item_tv)
//
//                }
//
//            }


        }

        holder.item_tv.text = drawerDatatypes[position].string_item
        if (drawerDatatypes[position].notification_badge_count != 0) {
            Glide.with(context)
                .load(drawerDatatypes[position].notification_badge_count)
                .into(holder.imgcheck!!)
        }

//        holder.imgcheck=drawerDatatypes[position].notification_badge_count

        /*SpannableString spannableString = new SpannableString(strings.get(position).getString_item());
        spannableString.setSpan(new UnderlineSpan(), 0, strings.get(position).getString_item().length(), 0);
        holder.item_tv.setText(spannableString);*/
        if (expandedPosition == position) {
            holder.item_parent_ll?.setBackgroundColor(Color.parseColor("#0168B3"))
//            holder.item_parent_ll?.setBackgroundColor(context.resources.getColor(R.color.colorPrimaryDark))
            holder.item_tv.setTypeface(null, Typeface.BOLD)
            //            holder.item_tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.more,
            //                    0);
            // holder.view1.setVisibility(View.VISIBLE);
        } else {
            holder.item_parent_ll?.setBackgroundColor(Color.parseColor("#00000000"))
            holder.item_tv.setTypeface(null, Typeface.NORMAL)
            //            holder.item_tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,0);
            //  holder.view1.setVisibility(View.INVISIBLE);
        }

//        if(expandedPosition==position){
//            holder.item_parent_ll?.setBackgroundColor(context.resources.getColor(R.color.colorPrimaryDark))
//        }

        //        if (position==3 && drawerDatatypes.get(position).getNotification_badge_count()!=0){
        //            holder.rl_noti_count.setVisibility(View.VISIBLE);
        //            holder.tv_noti_count.setText(""+drawerDatatypes.get(position).getNotification_badge_count());
        //        }else {
        //            holder.rl_noti_count.setVisibility(View.GONE);
        //        }

    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun getItemCount(): Int {
        return drawerDatatypes.size
    }

    inner class ViewHolder
    //        ImageView notification_count_img;

        (itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal var item_parent_ll: RelativeLayout
        internal var rl_noti_count: RelativeLayout? = null
        internal var item_tv: TextView
        internal var tv_noti_count: TextView? = null
        internal var view1: View? = null
        internal var imgcheck: ImageView? = null

        init {

            item_parent_ll = itemView.findViewById(R.id.rl_sidemenue_dashboard)
            item_tv = itemView.findViewById(R.id.item_tv)
            imgcheck = itemView.findViewById(R.id.check)
            //            view1 = itemView.findViewById(R.id.view1);
            //            rl_noti_count = itemView.findViewById(R.id.rl_noti_count);
            //            tv_noti_count = itemView.findViewById(R.id.tv_noti_count);
            //            notification_count_img = itemView.findViewById(R.id.notification_count_img);
        }


    }

    fun setonClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }


    fun selectItem(position: Int) {
        expandedPosition = position
        notifyDataSetChanged()
    }


    fun changeNotificationBadge(badge_count: String) {
        drawerDatatypes[4].notification_badge_count = Integer.parseInt(badge_count)
        notifyItemChanged(4)
    }


}