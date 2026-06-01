import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.JPanel;

class DrawPanel extends JPanel implements MouseListener {

    private Deck d;
    private Card[][] cards;
    private Rectangle replaceHitbox;
    private Rectangle restartHitbox;


    public DrawPanel() {

        cards = new Card[3][3];
        d = new Deck();
        for (int r = 0; r < cards.length; r++) {
            for (int c = 0; c < cards.length; c++) {
                cards[r][c] = d.getRandomCard();
            }
        }
        this.addMouseListener(this);
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int x = 50;
        int y = 10;
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(2.0f));
        Font timesNewRoman = new Font("Times New Roman", Font.BOLD, 24);
        g.setFont(timesNewRoman);

        g.drawRect(300,10, 155, 35);
        replaceHitbox = new Rectangle(300,10, 155, 35);
        g.drawString("Replace Cards", 302, 35);
        g.drawRect(85, 290, 155, 35);
        restartHitbox = new Rectangle(85,290, 155, 35);
        g.drawString("Play Again", 105, 315);

        g2d.setStroke(new BasicStroke(1.0f));
        for (int r = 0; r < cards.length; r++) {
            for (int c = 0; c < cards.length; c++) {
                g.drawImage(cards[r][c].getImage(), x, y, null);
                Rectangle cardHitBox = new Rectangle(x, y, cards[r][c].getImage().getWidth(), cards[r][c].getImage().getHeight());
                cards[r][c].setHitbox(cardHitBox);
                if (cards[r][c].getHighlight()) {
                    g.drawRect(x, y, (int)cardHitBox.getWidth(), (int)cardHitBox.getHeight());
                }
                x += 80;
            }
            y += 100;
            x = 50;
        }

        g.drawString("Number of cards left: " + d.getDeck().size(), x-10, y + 50);
        if  (lost()){
            g.drawString("YOU LOSE ", x-5, y + 80);
        }
    }

    public void mousePressed(MouseEvent e) {

        Point p = e.getPoint();
        int button = e.getButton();
        ArrayList<Card> selectedCards = new ArrayList<Card>();

        for(Card[] cardRow: cards) {
            for (Card card : cardRow) {
                if (card.getHighlight()) {
                    selectedCards.add(card);
                }
            }
        }

        if (replaceHitbox.contains(p) && button == 1){
            if (canReplace(selectedCards)){
                for (int r = 0; r < cards.length; r++) {
                    for (int c = 0; c < cards.length; c++) {
                        if (cards[r][c].getHighlight()) {
                            cards[r][c] = d.getRandomCard();
                        }
                    }
                }
            }
        } else if (restartHitbox.contains(p) && button == 1) {
            System.out.println(1);
            d = new Deck();
            for (int r = 0; r < cards.length; r++) {
                for (int c = 0; c < cards.length; c++) {
                    cards[r][c] = d.getRandomCard();
                }
            }
        } else {
            for (int r = 0; r < cards.length; r++) {
                for (int c = 0; c < cards.length; c++) {
                    if (cards[r][c].getHitbox().contains(p) && (button == 1 || button == 3)) {
                        cards[r][c].flipHighlight();
                        ;
                    }
                }
            }
        }



    }

    public boolean canReplace(ArrayList<Card> cards) {
        boolean jack = false;
        boolean queen = false;
        boolean king = false;
        int total = 0;
        for (Card card : cards) {
            try {
                if (card.getValue() == "A") {
                    total += 1;
                } else {
                    total += Integer.parseInt(card.getValue());
                }
            } catch (NumberFormatException _) {
                if (card.getValue().equals("J")) {
                    jack = true;
                } else if (card.getValue().equals("Q")) {
                    queen = true;
                } else if (card.getValue().equals("K")) {
                    king = true;
                }
            }
        }
        if (jack && queen && king && cards.size() == 3){
            return true;
        } else if (total == 11 && cards.size() == 2) {
            return true;
        } else {
            return false;
        }
    }

    public boolean lost(){
        int val = 0;
        boolean jack = false;
        boolean queen = false;
        boolean king = false;
        ArrayList<Integer> possibleValues = new ArrayList<Integer>();
        for(Card[] cardRow: cards) {
            for (Card card : cardRow) {
                try {
                    val = Integer.parseInt(card.getValue());
                } catch (NumberFormatException _){
                    if (card.getValue().equals("J")) {
                        val = 11;
                    } else if (card.getValue().equals("Q")) {
                        val = 12;
                    } else if (card.getValue().equals("K")) {
                        val = 13;
                    } else if (card.getValue().equals("A")) {
                        val = 1;
                    }
                }
                if (!possibleValues.contains(val)) {
                    possibleValues.add(val);
                }
            }
        }

        for (int value: possibleValues){
            int complement = 11 - value;
            for(Card[] cardRow: cards) {
                for (Card card : cardRow) {
                    try {
                        if (Integer.parseInt(card.getValue()) == complement) {
                            return false;
                        }
                    } catch (NumberFormatException _) {
                        if (card.getValue().equals("J")) {
                            jack = true;
                        } else if (card.getValue().equals("Q")) {
                            queen = true;
                        } else if (card.getValue().equals("K")) {
                            king = true;
                        }
                        if (jack && queen && king){
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    public void mouseReleased(MouseEvent e) { }
    public void mouseEntered(MouseEvent e) { }
    public void mouseExited(MouseEvent e) { }
    public void mouseClicked(MouseEvent e) { }
}