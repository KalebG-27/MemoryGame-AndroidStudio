package com.example.memorygame

//Handling everything that has to do with the functioning of the gameplay

class MemoryGame (private val boardSize: BoardSize) {

    val cards: List<MemoryCard>
    var numPairsFound = 0

    private var numCardsFlips = 0
    private  var  indexOfSingleSelectedCard: Int? = null


    // constructing the list of cards based on the board size
    init {
        val chosenImages = DEFFAULT_ICONS.shuffled().take(boardSize.getNumPairs())
        val randomizedImages = (chosenImages + chosenImages).shuffled()
        cards = randomizedImages.map { MemoryCard(it) }
    }

        // face up = face down
        // face down = face up
    fun flipCard(position: Int): Boolean {
            numCardsFlips++
        val card: MemoryCard = cards[position]
        // Three cases:
        // 0 cards previously flipped over => restore cards + flip over the selected card
        // 1 card previously flipped over => flip over the selected card + check if the images match
        // 2 cards previously flipped over => restore cards = flip over the selected card
        val foundMatch = false
        if (indexOfSingleSelectedCard == null) {
            // 0 or 2 cards previously flipped over
            restoreCards()
            indexOfSingleSelectedCard = position
        } else {
            // exactly 1 card previously flipped over
            val foundMatch: Boolean = checkForMatch(indexOfSingleSelectedCard!!, position)
            indexOfSingleSelectedCard = null
        }
        card.isFaceUp = !card.isFaceUp
        return foundMatch

    }

    private fun checkForMatch(position1: Int, position2: Int): Boolean {
        if (cards[position1].identifier != cards[position2].identifier) {
            return false
        }
        cards[position1].isMatched = true
        cards[position2].isMatched = true
        numPairsFound++
        return true
    }

    // this function will return the cards to default position based on if a match is found or not
    private fun restoreCards() {
        for (card:MemoryCard in cards) {
            if (!card.isMatched) {
                card.isFaceUp = false
            }
        }
    }

    // initiation the toast/ snackbar if a pair is found
    // returning whether true or false
    fun haveWonGame(): Boolean {
        return numPairsFound == boardSize.getNumPairs()
    }

    // returning whether true or false whether the card is face up
    fun isCardFaceUp(position: Int): Boolean {
        return cards[position].isFaceUp
    }

    fun getNumMoves(): Int {
        return numCardsFlips / 2
    }
}

