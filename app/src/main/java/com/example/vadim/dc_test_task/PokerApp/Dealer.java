package com.example.vadim.dc_test_task.PokerApp;

import android.content.Context;
import android.util.Log;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Dealer extends Player {

    private List<Card> cards = new ArrayList<Card>();

    public Dealer (String name, Context context) {
        super(name, context);
    }

    public void shuffle(){
        Log.d("Dealer", "Shuffling cards");
        cards = getCardDeck();
        Collections.shuffle(cards);
    }

    public void dealCards(Player player) {
        Log.d("Dealer", "Dealing cards to " + player.getName());
        for (int i = 0; i<5; i++) {
            player.giveCard(cards.get(i));
        }
        cards = cards.subList(5, cards.size());
    }

    public void dealDummyCards(Player player){
        for (int i = 0; i<5; i++) {
            player.giveCard(cards.get(i));
        }
    }

    private List<Card> getCardDeck(){
        ArrayList<Card> cards = new ArrayList<Card>();
        for (Card.Suits s : Card.Suits.values()){
            for (int i = 0; i<Card.ranks.length; i++){
                Card card = new Card(s, Card.ranks[i]);
                cards.add(card);
            }
        }
        return cards;
    }

    public void distributePot(int pot, Player[] players) {

        for (Player player : players) {
            player.addChips(pot / players.length);
        }

    }
}