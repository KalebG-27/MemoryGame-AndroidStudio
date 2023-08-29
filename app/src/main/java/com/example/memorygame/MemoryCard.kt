package com.example.memorygame

// This code below will be in charge of the cards in default
// this is making sure that the cards are faced down an no pairs have been solved
// to indicate that we are starting a new game
// Data class memory refers to an individual card
data class MemoryCard(
    val identifier: Int,
    var isFaceUp: Boolean = false,
    var isMatched: Boolean = false
)
