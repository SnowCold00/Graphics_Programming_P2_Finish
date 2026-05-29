import java.util.ArrayList;

public class Deck {
    private ArrayList<Card> deck;

    public Deck() {
        deck = new ArrayList<Card>();
        String[] suits = {"clubs", "diamonds", "hearts", "spades"};
        String[] values = {"02", "03", "04", "05", "06", "07", "08", "09", "10", "A", "J", "K", "Q"};

        for (String s : suits) {
            for (String v : values) {
                deck.add(new Card(s, v));
            }
        }
    }

    public Card getRandomCard() {
        int random = (int)(Math.random() * deck.size());
        return deck.remove(random);
    }

    public ArrayList<Card> getDeck() {
        return deck;
    }
}
