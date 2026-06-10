import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import javax.swing.JPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import javax.swing.Timer;

class DrawPanel extends JPanel implements ActionListener, MouseListener {

    private Deck d;
    private Card[][] cards;
    private Rectangle replaceHitbox;
    private Rectangle restartHitbox;
    private Timer time;
    private boolean cardAnimation;
    private ArrayList<int[]> missingCards;
    private int[] animationCord;
    private int[] cordGoal;
    private boolean turnAniEnd;
    private double turnScale;
    private boolean turnAniStart;
    private boolean scaleDirection;
    private int[] currentCard;
    private Card cardMoving;



    public DrawPanel() {

        cards = new Card[3][3];
        d = new Deck();
        for (int r = 0; r < cards.length; r++) {
            for (int c = 0; c < cards.length; c++) {
                cards[r][c] = d.getRandomCard();
            }
        }
        this.addMouseListener(this);
        cardAnimation = false;
        time = new Timer(7, this);
        animationCord= new int[]{300, 100};
        missingCards = new ArrayList<>();
        turnAniEnd = false;
        turnScale = 1;
        turnAniStart = false;
        scaleDirection = false;
        currentCard = new int[]{0, 0};

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

        Card backCard = new Card();
        if (!d.getDeck().isEmpty()) {
            g.drawImage(backCard.getImage(), 300, 100, null);
        }

        if (cardAnimation){
            g.drawImage(backCard.getImage(), animationCord[0], animationCord[1], null);
        } else if (turnAniStart){
            Graphics2D tempG2d = (Graphics2D) g2d.create();
            AffineTransform old = tempG2d.getTransform();
            tempG2d.translate(animationCord[0] + 40, animationCord[1]);
            tempG2d.scale(turnScale,1);
            if (scaleDirection) {
                tempG2d.drawImage(cardMoving.getImage(), -40 , 0, null);
            } else if (!scaleDirection){
                tempG2d.drawImage(backCard.getImage(), -40, 0, null);
            }
            tempG2d.setTransform(old);
            tempG2d.dispose();
        }


        g2d.setStroke(new BasicStroke(1.0f));
        for (int r = 0; r < cards.length; r++) {
            for (int c = 0; c < cards.length; c++) {
                if (!(cards[r][c].getValue().equals("0"))) {
                    g.drawImage(cards[r][c].getImage(), x, y, null);
                    Rectangle cardHitBox = new Rectangle(x, y, cards[r][c].getImage().getWidth(), cards[r][c].getImage().getHeight());
                    cards[r][c].setHitbox(cardHitBox);
                    if (cards[r][c].getHighlight()) {
                        g.drawRect(x, y, (int) cardHitBox.getWidth(), (int) cardHitBox.getHeight());
                    }
                }
                x += 80;
            }
            y += 100;
            x = 50;
        }

        g.drawString("Number of cards left: " + d.getDeck().size(), x-10, y + 50);
        if  (lost() && !time.isRunning()){
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

        if (replaceHitbox.contains(p) && button == 1){ // start animation
            if (canReplace(selectedCards)){
                for (int r = 0; r < cards.length; r++) {
                    for (int c = 0; c < cards.length; c++) {
                        if (cards[r][c].getHighlight()) { // removes cards once replaced
                            cards[r][c] = new Card(); //creates a card with value 0 placeholder
                            //System.out.println(r + c);
                            missingCards.add(new int[]{r, c});
                            //System.out.println(cards[r][c]);
                        }
                    }
                }
                time.start();
            }
        } else if (restartHitbox.contains(p) && button == 1) {
            System.out.println(1);
            d = new Deck();
            for (int r = 0; r < cards.length; r++) {
                for (int c = 0; c < cards.length; c++) {
                    cards[r][c] = d.getRandomCard();
                }
            }
            animationCord= new int[]{300, 100};
            missingCards = new ArrayList<>();
            time.stop();
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

    @Override
    public void actionPerformed(ActionEvent e) { //moves the entire grid or resizes the entire grid. Turn scale goes negative as well.

        if (!missingCards.isEmpty()){
            cardAnimation = true;
            cordGoal = new int[] {50+80*(missingCards.getFirst()[1]), 10+100*(missingCards.getFirst()[0])};
            if (animationCord[0] != cordGoal[0]) {
                animationCord[0] -= 1;
            }
            if (animationCord[1] != cordGoal[1]) {
                if (animationCord[1] < cordGoal[1]) {
                    animationCord[1] += 1;
                } else if (animationCord[1] > cordGoal[1]){
                    animationCord[1] -= 1;
                }
            }
            //System.out.println(animationCord[0]);
            //System.out.println(animationCord[1]);
            if (cordGoal[0] == animationCord[0] && cordGoal[1] == animationCord[1]) {
                cardAnimation = false;
                if (cards[missingCards.getFirst()[0]][missingCards.getFirst()[1]].getValue().equals("0")) {//starts the flip
                    turnAniStart = true;
                }
                if (turnAniStart){
                    if (!scaleDirection) {
                        turnScale -= 0.01;
                        if (turnScale <= 0.05) {
                            scaleDirection = true;
                            cardMoving = d.getRandomCard();
                            currentCard = missingCards.getFirst();
                        }
                    }else if (scaleDirection){
                        turnScale += 0.01;
                        if (turnScale >= 0.95){
                            turnAniEnd = true;
                        }
                    }
                }
                if (turnAniEnd) { //doesnt run
                    int[] temp = missingCards.removeFirst();
                    cards[temp[0]][temp[1]] = cardMoving;
                    turnAniStart = false;
                    turnScale = 1;
                    turnAniEnd = false;
                    scaleDirection = false;
                    for (int[] card : missingCards) {
                        System.out.println(Arrays.toString(card) + "1");
                    }
                    System.out.println("reset");
                    System.out.println();
                    animationCord = new int[]{300, 100};
                    //int[] temp = missingCards.removeFirst();
                    //cards[temp[0]][temp[1]] = d.getRandomCard();
                }
            }

        } else {
            cardAnimation = false;
            time.stop();
        }
    }
}