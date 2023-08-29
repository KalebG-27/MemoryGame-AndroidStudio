package com.example.memorygame

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import java.lang.Integer.min

class MemoryBoardAdapter(
    private val context: Context,
    private val boardSize: BoardSize,
    private val cards: List<MemoryCard>,
    private val cardClickListener: CardClickListener
) :
    RecyclerView.Adapter<MemoryBoardAdapter.ViewHolder>() {

    // creating a constant to allow the spacing between the cards
    // defining the margin size
    companion object  {
        private const val MARGIN_SIZE = 10
        private const val TAG = "MemoryBoardAdapter"
    }

    interface cardClickedList {
        fun onCardClicked(position: Int)
    }

    // creating one view of the recycler view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        //** creating variables that will allocate for the cardViews width and height
        // defining both with comparison to the parent allows for the cards to be flush with the
        // bottom on the screen
        val cardWidth = parent.width / boardSize.getWidth() - (2 * MARGIN_SIZE)
        val cardHeight = parent.height / boardSize.getHeight() - (2 * MARGIN_SIZE)
        val cardSideLength = min (cardWidth, cardHeight)
        val view = LayoutInflater.from(context).inflate(R.layout.memory_card, parent, false)
        val layoutParams =  view.findViewById<CardView>(R.id.cardView).layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.width = cardSideLength
        layoutParams.height = cardSideLength
        layoutParams.setMargins(MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE)     // setting margin to all 4 sides of card

        return ViewHolder(view)
    }


    // defining how many elements is in the recycler view
    override fun getItemCount() = boardSize.numCards

    // Taking the position data and binding it to the view holder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageButton = itemView.findViewById<ImageButton>(R.id.imageButton)


        fun bind(position: Int) {

            // if card is face up , display the image
            // else display the green background of the card
            val memoryCard = cards[position]
            imageButton.setImageResource(if (cards[position].isFaceUp) cards[position].identifier else R.drawable.ic_launcher_background)

            // how visible is the memory button
            // changing the float value
            imageButton.alpha = if (memoryCard.isMatched) .4f else 1.0f

            // is the matched pair is found, set a color indicator to show that the cards can no longer be flipped again
            // changing the card color to a grey color
            val colorStateList = if (memoryCard.isMatched) ContextCompat.getColorStateList(context, R.color.color_gray) else null
            ViewCompat.setBackgroundTintList(imageButton,colorStateList)

            imageButton.setOnClickListener {
                Log.i(TAG, "clicked on position $position")
                cardClickListener.onCardClicked(position)
            }

        }
    }

    open class CardClickListener {
        open fun onCardClicked(position: Int) {

        }
    }

}

