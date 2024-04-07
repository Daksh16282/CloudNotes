package com.example.cloudnotes

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class NotesAdapter(context: Context,dataset:ArrayList<NotesEntity>,activity: MainActivity) : RecyclerView.Adapter<NotesAdapter.ViewHolder>() {

   private var dataset: ArrayList<NotesEntity>
    private  var context:Context
    private  var activity:MainActivity
    var selectFlag=false

    var indexSet:ArrayList<Int> = ArrayList()
    init {
        this.context=context
        this.dataset=dataset
        this.activity=activity
    }
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
               private var headingCard:TextView = itemView.findViewById(R.id.headingCard)
               private var contentCard:TextView = itemView.findViewById(R.id.contentCard)
               private var card:CardView=itemView.findViewById(R.id.cardView)
                private var check:ImageView = itemView.findViewById(R.id.check)
        //By defining flag in viewholder,it works differently for each recyclerview item
            var reselectFlag=false
             //Getters and setters for data
        fun getCheck():ImageView{
            return check
        }
            fun getHeading():TextView{
                return headingCard
            }
            fun getContent():TextView{
                return contentCard
            }
            fun setHeading(data:String){
                headingCard.text=data
            }
            fun setContent(data:String){
                contentCard.text=data
            }
            fun getCard():CardView{
                return card
            }
            fun setReselFlag(value:Boolean){
                reselectFlag=value
            }
            fun getReselFlag():Boolean{
                return reselectFlag
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //Inflating Layout
        var view = LayoutInflater.from(context).inflate(R.layout.note_card,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //Binding Data
        holder.setHeading(dataset.get(holder.adapterPosition).heading)
        holder.setContent(dataset.get(holder.adapterPosition).content)

        holder.itemView.setOnClickListener {
            //Show data on single click
            if(!selectFlag) {
                val intent = Intent(context, WriteNote::class.java)
                intent.putExtra("notesHead", dataset.get(holder.adapterPosition).heading)
                intent.putExtra("notesContent", dataset.get(holder.adapterPosition).content)
                intent.putExtra("notesPrimaryKey", dataset.get(holder.adapterPosition).primaryKey)
                intent.putExtra("noteposition", holder.adapterPosition)
                context.startActivity(intent)
            }
            //Select multiple if one is already selected using long click listener
            else{
                if(!holder.getReselFlag()) {
                    holder.getCheck().visibility = View.VISIBLE
                    holder.setReselFlag(true)
                    indexSet.add(dataset.get(holder.adapterPosition).primaryKey)
                }else{
                    holder.getCheck().visibility = View.INVISIBLE
                    holder.setReselFlag(false)
                    var status=indexSet.remove(dataset.get(holder.adapterPosition).primaryKey)
                    if(indexSet.size==0){
                        activity.getDelete().visibility=View.INVISIBLE
                        activity.getCancle().visibility=View.INVISIBLE
                        selectFlag=false
                    }
                }

            }
        }
        //Long press to select first
        holder.itemView.setOnLongClickListener { //adding index of first selected
            indexSet.add(dataset.get(holder.adapterPosition).primaryKey)
            holder.getCheck().visibility = View.VISIBLE

            if (!holder.getReselFlag()) {
                holder.setReselFlag(true)
            } else {
                holder.setReselFlag(false)
            }
            //Cancel and delete appears
            selectFlag = true


            activity.getDelete().visibility = View.VISIBLE
            activity.getCancle().visibility = View.VISIBLE
            true
        }


    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    fun setFilteredList(filteredList:ArrayList<NotesEntity>) {
        this.dataset=filteredList
        notifyDataSetChanged()
    }

    //Send primarykeys of selected notes to mainactivity to delete them
    fun getPKSet():ArrayList<Int>{
        return indexSet
    }

    //set select flag false on clicking cross,calling it in mainactivity as updateUi working in mainactivity only
    fun setselFlag(value: Boolean){
        selectFlag=value
    }


}


