package com.example.memorygame

import android.animation.ArgbEvaluator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }
    private lateinit var clRoot: ConstraintLayout
    private lateinit var gameBoard : RecyclerView
    private lateinit var numMoves : TextView
    private lateinit var numPairs : TextView

    private lateinit var memoryGame: MemoryGame
    private lateinit var adapter: MemoryBoardAdapter
    private var boardSize = BoardSize.EASY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        clRoot = findViewById(R.id.clRoot)
        gameBoard = findViewById(R.id.gameBoard)
        numMoves = findViewById(R.id.numMoves)
        numPairs = findViewById(R.id.numPairs)

        setupBoard()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    // this function will warn the user when they are about ot quit the game and their progess will be erased
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mi_refresh -> {
                if (memoryGame.getNumMoves() > 0 && !memoryGame.haveWonGame()) {
                    showAlertDialog("Quit your current game?", null, View.OnClickListener {
                        setupBoard()
                    })
                } else {
                    setupBoard()
                }
                return true
            }
            R.id.mi_new_size -> {
                showNewSizeDialog()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showNewSizeDialog() {
        val boardSizeView = LayoutInflater.from(this).inflate(R.layout.dialog_board_size, null)
        val radioGroupSize = boardSizeView.findViewById<RadioGroup>(R.id.radioGroup)
        when (boardSize) {              // depending on the board size, I am telling the board size which item need to be checked
            BoardSize.EASY -> radioGroupSize.check(R.id.rbEasy)             // the current board size should automatically show up in the dialogue
            BoardSize.MEDIUM -> radioGroupSize.check(R.id.rbMedium)
            BoardSize.HARD -> radioGroupSize.check(R.id.rbHard)
        }
        showAlertDialog("Choose new size", boardSizeView, View.OnClickListener {
            boardSize = when (radioGroupSize.checkedRadioButtonId) {
                R.id.rbEasy -> BoardSize.EASY
                R.id.rbMedium -> BoardSize.MEDIUM
                else -> BoardSize.HARD
            }
            setupBoard()
        })
    }

    private fun showAlertDialog(title: String, view: View?, positiveClickListener: View.OnClickListener) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(view)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("OK") { _, _ ->
                positiveClickListener.onClick(null)
            }.show()
    }

    // depending on the board size we would like to reset the value of the text views
    private fun setupBoard() {

        memoryGame = MemoryGame(boardSize)
        when (boardSize) {
            BoardSize.EASY -> {
                numMoves.text = "Easy: 4 x 2"
                numPairs.text = "Pairs: 0/4"
            }
            BoardSize.MEDIUM -> {
                numMoves.text = "Medium: 6 x 3"
                numPairs.text = "Pairs: 0/9"
            }
            BoardSize.HARD -> {
                numMoves.text = "Hard: 6 x 4"
                numPairs.text = "Pairs: 0/12"
            }
        }
        numPairs.setTextColor(ContextCompat.getColor(this, R.color.color_progress_none))
        adapter = MemoryBoardAdapter(this, boardSize,memoryGame.cards, object:
            MemoryBoardAdapter.CardClickListener() {
            override fun onCardClicked(position: Int) {
                updateGameWithFlip(position)    // checking if  two card in flipped position are correct/ matches
            }
        }

        )
        gameBoard.adapter = adapter
        gameBoard.setHasFixedSize(true)
        gameBoard.layoutManager = GridLayoutManager(this, boardSize.getWidth())

    }

    // this function is responsible for updating the memory game with an attempted flip at this position
    // if conditions are met, the application will display a TOAST/ Snack bar
    // returning the change to the game that a pir has been matched or not
    private fun updateGameWithFlip(position: Int) {
        // Error checking
        if (memoryGame.haveWonGame()) {
            Snackbar.make(clRoot, "You already won!", Snackbar.LENGTH_LONG).show()
            return
        }
        if (memoryGame.isCardFaceUp(position)) {
            Snackbar.make(clRoot, "Invalid move!", Snackbar.LENGTH_SHORT).show()
            return
        }

        // Actually flip the card
        if (memoryGame.flipCard(position)) {
            Log.i(TAG, "Found a match! Num pairs found: ${memoryGame.numPairsFound}")

            // color indication of the progress of pairs found
            // Adding color interpolation
            val color = ArgbEvaluator().evaluate(
                memoryGame.numPairsFound.toFloat() / boardSize.getNumPairs(),
                ContextCompat.getColor(this, R.color.color_progress_none),
                ContextCompat.getColor(this, R.color.color_progress_full)
            ) as Int
            numPairs.setTextColor(color)
            numPairs.text = "Pairs: ${memoryGame.numPairsFound} / ${boardSize.getNumPairs()}"
            if (memoryGame.haveWonGame()) {
                Snackbar.make(clRoot, "You won! Congratulations.", Snackbar.LENGTH_LONG).show()
            }
        }
        numMoves.text = "Moves: ${memoryGame.getNumMoves()}"
        adapter.notifyDataSetChanged()
    }

    }
