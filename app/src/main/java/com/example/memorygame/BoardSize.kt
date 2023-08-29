package com.example.memorygame

// Modelview for  BoardSize
// incorporating "enum" allow the intake of parameters
enum class BoardSize(val numCards: Int) {
    EASY(8),
    MEDIUM(18),
    HARD(24);


    //changing the number of cards as the level of difficulty increases
    fun getWidth(): Int {
        return when (this) {
            EASY -> 2
            MEDIUM -> 3
            HARD -> 4
        }
    }

    // changing the number of cards by height on the grid
    // the board size will increase based on the number of card
    fun getHeight(): Int {
        return numCards / getWidth()
    }

    // determining how many corresponding matches will have based on the level of difficulty
    fun getNumPairs(): Int {
        return numCards / 2
    }

}